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
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import com.fasterxml.jackson.datatype.jsr310.deser.key.ZoneOffsetKeyDeserializer;
import com.getaroom.app.entity.Today;
import com.getaroom.app.entity.Room;
import com.getaroom.app.repository.HistoryRepository;
import com.getaroom.app.repository.StatusRepository;
import com.getaroom.app.repository.TodayRepository;

// Remove the 'exclude' after the database backend has been done
//@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@SpringBootApplication
public class App implements CommandLineRunner {

	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

	private final TodayRepository todayRepository;
	private final StatusRepository statusRepository;
	private final HistoryRepository historyRepository;

	@Autowired
	public App(TodayRepository todayRepository, StatusRepository statusRepository, HistoryRepository historyRepository) {
		this.todayRepository = todayRepository;
		this.statusRepository = statusRepository;
		this.historyRepository = historyRepository;
	}

	public static void main(String[] args) {
		SpringApplication.run(App.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		// Schelude the batch processing of 'today' MongoDB collection into 'history' collection
		// ZonedDateTime already handles daylight saving cases
		ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Europe/Lisbon"));
		
		ZonedDateTime nextRun = now.withHour(0).withMinute(0).withSecond(0);
		if (now.compareTo(nextRun) > 0)
			nextRun = nextRun.plusDays(1);
		
		long initialDelay = Duration.between(now, nextRun).getSeconds();

		ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
		scheduler.scheduleAtFixedRate(new TodayMigrateToHistoryBatch(),
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
			if (!verifyDocumentType(doc, List.of("room", "occupacy", "maxNumberOfPeople")))
				return;

			String room = (String) doc.get("room");
			Room status = statusRepository.findByRoom(room)
					.orElse(new Room(room));
			status.setOccupacy((double) doc.get("occupacy"));
			status.setMaxNumberOfPeople((int) doc.get("maxNumberOfPeople"));

			statusRepository.save(status);
		}));
		mqttClient.subscribe("event/#", 2, (topic, msg) -> uploadToDatabase(msg, (doc) -> {
			System.out.println("Obtaining EVENT document");
			if (!verifyDocumentType(doc, List.of("time", "user", "email", "room", "entered")))
				return;

			String timeStr = (String) doc.get("time");
			try {
				Date time = dateFormat.parse(timeStr);
				todayRepository.save(new Today(
						(String) doc.get("user"),
						(String) doc.get("email"),
						(String) doc.get("room"),
						(boolean) doc.get("entered"),
						time
				));
			} catch (ParseException e) {
				System.err.println("Error: Failure parsing date in Today object: " + timeStr);
				e.printStackTrace();
			}
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

	private class TodayMigrateToHistoryBatch implements Runnable {

		@Override
		public void run() {
			List<Today> todays = todayRepository.findAll();
			historyRepository.saveAll( todays.stream().map(Today::cloneHistory).toList() );
			todayRepository.deleteAll( todays );
		}

	}
}
