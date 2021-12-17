package com.fetcher.app;

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
import java.util.Date;
import java.util.concurrent.CountDownLatch;

// Remove the 'exclude' after the database backend has been done
//@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@SpringBootApplication
public class App implements CommandLineRunner {

	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

	private final TodayRepository todayRepository;
	private final StatusRepository statusRepository;

	@Autowired
	public App(TodayRepository todayRepository, StatusRepository statusRepository) {
		this.todayRepository = todayRepository;
		this.statusRepository = statusRepository;
	}

	public static void main(String[] args) {
		SpringApplication.run(App.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		String name = "fetcher";
		IMqttClient mqttClient;

		System.out.print("Creating...");
		mqttClient = new MqttClient("tcp://mosquitto:1883", name);
		System.out.println(" done!");

		System.out.print("Connecting...");
		MqttConnectOptions mqttOptions = new MqttConnectOptions();
		mqttOptions.setAutomaticReconnect(true);
		mqttOptions.setCleanSession(true);
		mqttOptions.setConnectionTimeout(10);
		mqttClient.connect(mqttOptions);
		System.out.println(" done!");

		CountDownLatch receivedMsg = new CountDownLatch(1);
		mqttClient.subscribe("mosquitto/test", 2, (topic, msg) -> {
			byte[] payload = msg.getPayload();
			String msgStr = byteArrayDecode(payload);
			System.out.println("Message: " + msgStr);

			Document doc = Document.parse(msgStr);
			String msgType = (String) doc.get("type");
			switch (msgType) {
				case "status" -> {
					String room = (String) doc.get("room");
					Status status = statusRepository.findByRoom(room)
							.orElse(new Status(room));
					status.setOccupacy((double) doc.get("occupacy"));
					status.setMaxNumberOfPeople((int) doc.get("maxNumberOfPeople"));

					statusRepository.save(status);
				}
				case "event" -> {
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
						return;
					}
				}
				default -> {
					System.err.println("Error: invalid message type " + msgType);
					return;
				}
			}

			System.out.println("Saved into database");

			// If this is commented, the program will run indefinitely
			//receivedMsg.countDown();
		});
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
}
