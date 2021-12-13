package com.brokerclient.app;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Component
public class MQTTClient implements InitializingBean {

    @Override
    public void afterPropertiesSet() throws Exception {
        String name = "fetcher";
        IMqttClient client;

        System.out.print("Creating...");
        client = new MqttClient("tcp://127.0.0.1:1883", name);
        System.out.println(" done!");

        System.out.print("Connecting...");
        MqttConnectOptions options = new MqttConnectOptions();
        options.setAutomaticReconnect(true);
        options.setCleanSession(false);
        options.setConnectionTimeout(10);
        client.connect(options);
        System.out.println(" done!");

        CountDownLatch receivedMsg = new CountDownLatch(10);
        client.subscribe("mosquitto/test", (topic, msg) -> {
            byte[] payload = msg.getPayload();
            String msgStr = byteArrayDecode(payload);
            System.out.println("Message: " + msgStr);
            receivedMsg.countDown();
        });
        receivedMsg.await(1, TimeUnit.MINUTES);
    }

    private String byteArrayDecode(byte[] bytes) {
        Charset charset = StandardCharsets.UTF_8;
        return charset.decode(ByteBuffer.wrap(bytes)).toString();
    }
}
