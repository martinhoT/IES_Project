package com.brokerclient.app;

import com.mongodb.client.result.InsertOneResult;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import com.mongodb.reactivestreams.client.MongoCollection;
import com.mongodb.reactivestreams.client.MongoDatabase;
import com.mongodb.reactivestreams.client.Success;
import org.bson.Document;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Component
// TODO: separate into two classes, a MQTT interface and a MongoDB interface (for organization)
public class MQTTClient implements InitializingBean {

    @Override
    public void afterPropertiesSet() throws Exception {
        // TODO: point to the MongoDB node
        MongoClient mongoClient = MongoClients.create("mongodb://localhost");
        // TODO: put the database's name accordingly
        MongoDatabase database = mongoClient.getDatabase("data_gather");

        MongoCollection<Document> collectionToday = database.getCollection("today");

        String name = "fetcher";
        IMqttClient mqttClient;

        System.out.print("Creating...");
        mqttClient = new MqttClient("tcp://127.0.0.1:1883", name);
        System.out.println(" done!");

        System.out.print("Connecting...");
        MqttConnectOptions mqttOptions = new MqttConnectOptions();
        mqttOptions.setAutomaticReconnect(true);
        mqttOptions.setCleanSession(false);
        mqttOptions.setConnectionTimeout(10);
        mqttClient.connect(mqttOptions);
        System.out.println(" done!");

        CountDownLatch receivedMsg = new CountDownLatch(10);
        mqttClient.subscribe("mosquitto/test", (topic, msg) -> {
            byte[] payload = msg.getPayload();
            String msgStr = byteArrayDecode(payload);
            System.out.println("Message: " + msgStr);

            collectionToday.insertOne( Document.parse(msgStr) ).subscribe(new Subscriber<>() {
                @Override
                public void onSubscribe(Subscription subscription) {
                    subscription.request(1);
                }

                @Override
                public void onNext(InsertOneResult insertOneResult) {
                    System.out.println("Inserted!");
                }

                @Override
                public void onError(Throwable throwable) {
                    System.out.println("Failed...");
                }

                @Override
                public void onComplete() {
                    System.out.println("Completed!");
                }
            });

            receivedMsg.countDown();
        });
        // TODO: ... what's this even doing?
        receivedMsg.await(1, TimeUnit.MINUTES);

        mqttClient.close();
        mongoClient.close();
    }

    private String byteArrayDecode(byte[] bytes) {
        Charset charset = StandardCharsets.UTF_8;
        return charset.decode(ByteBuffer.wrap(bytes)).toString();
    }
}
