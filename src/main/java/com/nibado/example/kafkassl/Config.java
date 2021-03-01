package com.nibado.example.kafkassl;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;

public class Config {
    public static final String TOPIC = "test";
    public static Producer<Integer, String> configureProducer() {
        return configureProducer("localhost:9092");
    }

    public static Producer<Integer, String> configureProducer(String... servers) {
        var serversString = String.join(",", servers);

        Properties config = new Properties();
        config.put("client.id", localHostname());
        config.put("bootstrap.servers", serversString);
        config.put("key.serializer", IntegerSerializer.class.getName());
        config.put("value.serializer", StringSerializer.class.getName());
        config.put("offsets.topic.replication.factor", "1");
        config.put("acks", "all");

        return new KafkaProducer<>(config);
    }

    public static Consumer<Integer, String> configureConsumer() {
        return configureConsumer("localhost:9092");
    }

    public static Consumer<Integer, String> configureConsumer(String... servers) {
        var serversString = String.join(",", servers);

        Properties config = new Properties();
        config.put("client.id", localHostname());
        config.put("group.id", "foo");
        config.put("bootstrap.servers", serversString);
        config.put("key.deserializer", IntegerDeserializer.class.getName());
        config.put("value.deserializer", StringDeserializer.class.getName());

        return new KafkaConsumer<>(config);
    }

    private static final String localHostname() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }
}
