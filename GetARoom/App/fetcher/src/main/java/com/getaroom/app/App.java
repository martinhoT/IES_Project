package com.getaroom.app;

import org.bson.Document;
import org.eclipse.paho.client.mqttv3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import com.getaroom.app.entity.mongodb.BlacklistNotification;
import com.getaroom.app.entity.mysql.Department;
import com.getaroom.app.entity.mongodb.EventNow;
import com.getaroom.app.entity.mysql.Room;
import com.getaroom.app.other.DepartmentInfo;
import com.getaroom.app.repository.mongodb.BlacklistNotificationRepository;
import com.getaroom.app.repository.mongodb.EventHistoryRepository;
import com.getaroom.app.repository.mongodb.StatusRepository;
import com.getaroom.app.repository.mysql.BlacklistRepository;
import com.getaroom.app.repository.mysql.DepartmentRepository;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.getaroom.app.repository.mongodb.EventRepository;
import com.getaroom.app.repository.mysql.RoomRepository;

@SpringBootApplication
@EnableSwagger2
public class App implements CommandLineRunner {

	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

	// MongoDB repositories
	private final StatusRepository statusRepository;
	private final EventRepository eventRepository;
	private final EventHistoryRepository eventHistoryRepository;
	private final BlacklistNotificationRepository blacklistNotificationRepository;
	
	// MySQL repositories
	private final BlacklistRepository blacklistRepository;
	private final DepartmentRepository departmentRepository;
	private final RoomRepository roomRepository;

	private final Gson gson;

	private Map<Integer, DepartmentInfo> departmentInfoMap;

	@Bean
	public Docket productApi() {
		return new Docket(DocumentationType.SWAGGER_2).select().apis(RequestHandlerSelectors.basePackage("com.getaroom.app")).build();
	}

	@Autowired
	public App(
		EventRepository todayRepository,
		StatusRepository statusRepository,
		EventHistoryRepository eventHistoryRepository,
		BlacklistNotificationRepository blacklistNotificationRepository,
		BlacklistRepository blacklistRepository,
		DepartmentRepository departmentRepository,
		RoomRepository roomRepository) {

		this.eventRepository = todayRepository;
		this.statusRepository = statusRepository;
		this.eventHistoryRepository = eventHistoryRepository;
		this.blacklistNotificationRepository = blacklistNotificationRepository;
		this.blacklistRepository = blacklistRepository;
		this.departmentRepository = departmentRepository;
		this.roomRepository = roomRepository;

		gson = new GsonBuilder()
			// This is required for correct Jackson serialization of the Date type 'time' attribute on BlacklistNotification
			.setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
			.create();

		// Obtain static department names for dynamic name assignment as STATUS messages are received
		FileReader f = null;
		try {
			f = new FileReader("src/main/resources/static/data/department_info.json");
		} catch (FileNotFoundException e) {
			System.err.println("File with room styles (department_info.json) could not be read");
		}

		departmentInfoMap = new HashMap<>();
		if (f != null) {
			JsonObject dInfoAll = gson.fromJson(f, JsonObject.class);
			for (Entry<String, JsonElement> dInfo : dInfoAll.entrySet())
				departmentInfoMap.put(Integer.parseInt(dInfo.getKey()), gson.fromJson(dInfo.getValue(), DepartmentInfo.class));
		}
	}

	public static void main(String[] args) {
		SpringApplication.run(App.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		// Schelude the daily batch processing
		// ZonedDateTime already handles daylight saving cases
		ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Europe/Lisbon"));
		
		ZonedDateTime nextRun = now.withHour(15).withMinute(30).withSecond(30);
		if (now.compareTo(nextRun) > 0)
			nextRun = nextRun.plusDays(1);
		
		long initialDelay = Duration.between(now, nextRun).getSeconds();

		ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
		scheduler.scheduleAtFixedRate(new DailyBatchProcessing(),
			initialDelay,
			TimeUnit.DAYS.toSeconds(1),
			TimeUnit.SECONDS);
		
		// Fetcher unit that reads data from the broker and puts into the MongoDB databases
		String name = "fetcher";
		IMqttClient mqttClient;

		System.out.print("Creating...");
		mqttClient = new MqttClient("tcp://mosquitto:1883", name);
		System.out.println(" done!");

		System.out.print("Connecting...");
		MqttConnectOptions mqttOptions = new MqttConnectOptions();
		mqttOptions.setAutomaticReconnect(false);
		mqttOptions.setCleanSession(true);
		mqttOptions.setConnectionTimeout(10);
		mqttClient.connect(mqttOptions);
		System.out.println(" done!");

		CountDownLatch receivedMsg = new CountDownLatch(1);
		mqttClient.subscribe("status/#", 2, (topic, msg) -> uploadToDatabase(msg, (doc) -> {
			System.out.println("Obtaining STATUS document");
			if (!verifyDocumentType(doc, List.of("room", "occupancy", "maxNumberOfPeople", "time")))
				return;

			try {
				// Obtain current room status on DB and the room on MySQL
				String roomStr = (String) doc.get("room");
				int departmentNum = Integer.parseInt(roomStr.split("\\.")[0]);
				Room room = roomRepository.findById(roomStr)
						.orElse(new Room(roomStr, departmentNum));
				
				// Update the current status
				String timeStr = (String) doc.get("time");
				Date time = null;
				try {
					time = dateFormat.parse(timeStr);
				} catch (ParseException e) {
					System.err.println("Error: Failure parsing date in EVENT object: " + timeStr);
				}
				room.setOccupancy( (double) doc.get("occupancy") );
				room.setMaxNumberOfPeople( (int) doc.get("maxNumberOfPeople") );
				room.setLastUpdateTime(time);
	
				// Update Department entity using static info
				Department department = departmentRepository.findById(departmentNum)
						.orElse(new Department(departmentNum));
				department.updateFromInfo( departmentInfoMap.get( departmentNum ) );
				departmentRepository.save( department );
	
				// Finally save/update the room's statistics
				roomRepository.save(room);
				statusRepository.save( room.createStatus() );
			} catch (Exception e) {
				e.printStackTrace();
			}
		}));
		mqttClient.subscribe("event/#", 2, (topic, msg) -> uploadToDatabase(msg, (doc) -> {
			System.out.println("Obtaining EVENT document");
			if (!verifyDocumentType(doc, List.of("time", "user", "email", "room", "entered")))
				return;

			String timeStr = (String) doc.get("time");
			Date time = null;
			try {
				time = dateFormat.parse(timeStr);
			} catch (ParseException e) {
				System.err.println("Error: Failure parsing date in EVENT object: " + timeStr);
			}

			if (time == null) return;
			
			String email = (String) doc.get("email");
			String room = (String) doc.get("room");
			boolean entered = (boolean) doc.get("entered");

			EventNow event = new EventNow( (String) doc.get("user"), email, room, entered, time );

			// Create a notification if this event matches a blacklist
			System.out.println("Is room " + room + " and email " + email + " blacklisted? " + isBlacklisted(room, email));
			if ( entered && isBlacklisted(room, email) ) {
				BlacklistNotification notification = BlacklistNotification.fromEvent(event);

				MqttMessage notificationMsg = new MqttMessage();
				notificationMsg.setQos(2);
				notificationMsg.setPayload( gson.toJson( notification ).getBytes(Charset.forName("UTF-8")) );
				notificationMsg.setRetained(false);

				try {
					mqttClient.publish("blacklist_notification", notificationMsg);
				} catch (MqttPersistenceException e) {
					System.err.println("Error: MQTT persistence exception when giving an alert for " + event);
				} catch (MqttException e) {
					System.err.println("Error: Unspecified MQTT exception when giving an alert for " + event);
				}

				blacklistNotificationRepository.save( notification );
			}
			eventRepository.save( event );
		}));
		receivedMsg.await();

		System.out.print("Await done, closing...");
		mqttClient.disconnect();
		mqttClient.close();
		System.out.println(" done!");
	}

	private String byteArrayDecode(byte[] bytes) {
		Charset charset = StandardCharsets.UTF_8;
		return charset.decode(ByteBuffer.wrap(bytes)).toString();
	}

	private void uploadToDatabase(MqttMessage msg, Consumer<Document> uploader) {
		byte[] payload = msg.getPayload();
		String msgStr = byteArrayDecode(payload);
		System.out.println("Message: " + msgStr);

		uploader.accept(Document.parse(msgStr));

		System.out.println("Saved into database");
	}

	// Verify that the message if formatted correctly. Already sends an error message
	private boolean verifyDocumentType(Document doc, List<String> fields) {
		if (!fields.stream().allMatch(doc::containsKey)) {
			System.err.println("Error: The message is doesn't contain all of the following attributes: " + fields + ".");
			return false;
		}
		return true;
	}

	// Simply returns if events of that room and email are blacklisted on the database
	private boolean isBlacklisted(String room, String email) {
		return blacklistRepository.existsByRoomAndEmail(room, email);
	}

	/**
	 * Daily batch processing of:
	 * - 'event' MongoDB collection into 'event_history' collection;
	 * - delete old seen notifications.
	 */
	private class DailyBatchProcessing implements Runnable {

		@Override
		public void run() {
			// 'event' into 'event_history'
			List<EventNow> todays = eventRepository.findAll();
			eventHistoryRepository.saveAll( todays.stream().map(EventNow::cloneHistory).toList() );
			eventRepository.deleteAll( todays );

			// delete seen notifications
			blacklistNotificationRepository.deleteBySeen(true);
		}

	}
}
