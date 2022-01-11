package com.getaroom.app;

import org.bson.Document;
import org.eclipse.paho.client.mqttv3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import com.getaroom.app.entity.BlacklistNotification;
import com.getaroom.app.entity.EventNow;
import com.getaroom.app.entity.StatusHistory;
import com.getaroom.app.entity.StatusNow;
import com.getaroom.app.repository.BlacklistNotificationRepository;
import com.getaroom.app.repository.BlacklistRepository;
import com.getaroom.app.repository.EventHistoryRepository;
import com.getaroom.app.repository.StatusRepository;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.getaroom.app.repository.EventRepository;
import com.getaroom.app.repository.StatusHistoryRepository;

@SpringBootApplication
public class App implements CommandLineRunner {

	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

	private final StatusRepository statusRepository;
	private final StatusHistoryRepository statusHistoryRepository;
	private final EventRepository eventRepository;
	private final EventHistoryRepository eventHistoryRepository;
	private final BlacklistNotificationRepository blacklistNotificationRepository;
	private final BlacklistRepository blacklistRepository;

	private final Gson gson;

	@Autowired
	public App(
		EventRepository todayRepository,
		StatusRepository statusRepository,
		EventHistoryRepository eventHistoryRepository,
		StatusHistoryRepository statusHistoryRepository,
		BlacklistNotificationRepository blacklistNotificationRepository,
		BlacklistRepository blacklistRepository) {

		this.eventRepository = todayRepository;
		this.statusRepository = statusRepository;
		this.eventHistoryRepository = eventHistoryRepository;
		this.statusHistoryRepository = statusHistoryRepository;
		this.blacklistNotificationRepository = blacklistNotificationRepository;
		this.blacklistRepository = blacklistRepository;

		this.gson = new GsonBuilder()
			// This is required for correct Jackson serialization of the Date type 'time' attribute on BlacklistNotification
			.setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
			.create();
	}

	public static void main(String[] args) {
		SpringApplication.run(App.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		// Schelude the batch processing of 'today' MongoDB collection into 'history' collection
		// ZonedDateTime already handles daylight saving cases
		ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Europe/Lisbon"));
		
		ZonedDateTime nextRun = now.withHour(15).withMinute(30).withSecond(30);
		if (now.compareTo(nextRun) > 0)
			nextRun = nextRun.plusDays(1);
		
		long initialDelay = Duration.between(now, nextRun).getSeconds();

		ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
		scheduler.scheduleAtFixedRate(new NowMigrateToHistoryBatch(),
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
			if (!verifyDocumentType(doc, List.of("room", "occupacy", "maxNumberOfPeople", "time")))
				return;

			// Obtain current room status on DB
			String room = (String) doc.get("room");
			StatusNow status = statusRepository.findByRoom(room)
					.orElse(null);
			try {
				// Put that status on the history collection
				if (status != null) {
					StatusHistory statusOld = status.cloneHistory();
					statusHistoryRepository.save(statusOld);
				}
				// If it was never in the repository, create a new one
				else
					status = new StatusNow(room);
				
				// Update the current status
				String timeStr = (String) doc.get("time");
				Date time = null;
				try {
					time = dateFormat.parse(timeStr);
				} catch (ParseException e) {
					System.err.println("Error: Failure parsing date in EVENT object: " + timeStr);
				}
				status.setOccupacy((double) doc.get("occupacy"));
				status.setMaxNumberOfPeople((int) doc.get("maxNumberOfPeople"));
				status.setTime(time);
				statusRepository.save(status);
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

	// TODO: allow for multiple security guards? Instead of this returning a boolean, returns a list of guards that blacklisted that event
	// Simply returns if events of that room and email are blacklisted on the database
	private boolean isBlacklisted(String room, String email) {
		return blacklistRepository.findByRoomIdAndEmail(room, email).orElse(null) != null;
	}

	private class NowMigrateToHistoryBatch implements Runnable {

		@Override
		public void run() {
			List<EventNow> todays = eventRepository.findAll();
			eventHistoryRepository.saveAll( todays.stream().map(EventNow::cloneHistory).toList() );
			eventRepository.deleteAll( todays );
		}

	}
}
