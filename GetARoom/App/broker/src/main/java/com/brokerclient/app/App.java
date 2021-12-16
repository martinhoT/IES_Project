package com.brokerclient.app;

import org.eclipse.paho.client.mqttv3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

// Remove the 'exclude' after the database backend has been done
//@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@SpringBootApplication
public class App implements CommandLineRunner {

	private final TodayRepository todayRepository;

	@Autowired
	public App(TodayRepository todayRepository) {
		this.todayRepository = todayRepository;
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
//        mqttClient = new MqttClient("tcp://localhost:1883", name);
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

			try {
				todayRepository.save(new Today(msgStr));
			} catch (Exception e) {
				e.printStackTrace();
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
