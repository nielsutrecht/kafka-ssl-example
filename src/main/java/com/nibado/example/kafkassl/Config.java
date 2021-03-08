package com.nibado.example.kafkassl;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;

import static org.apache.kafka.clients.CommonClientConfigs.SECURITY_PROTOCOL_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.*;
import static org.apache.kafka.common.config.SslConfigs.*;

public class Config {
    public static final String TOPIC = "test";
    public static final String PASSWORD = "mysecretpassword";
    public static final int PLAIN_PORT = 9092;
    public static final int SSL_PORT = 9093;
    public static final String CLIENT_TRUSTSTORE = jksFile("./secrets/kafka.client.truststore.jks");
    public static final String CLIENT_KEYSTORE = jksFile("./secrets/kafka.client.keystore.jks");
    public static final String TIMEOUT_MS = "5000";

    public static Properties producerProperties(boolean ssl) {
        Properties config = new Properties();

        config.put(CLIENT_ID_CONFIG, localHostname());

        config.put(KEY_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class.getName());
        config.put(VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        config.put(SOCKET_CONNECTION_SETUP_TIMEOUT_MAX_MS_CONFIG, TIMEOUT_MS);
        config.put(REQUEST_TIMEOUT_MS_CONFIG, TIMEOUT_MS);
        config.put(DELIVERY_TIMEOUT_MS_CONFIG, TIMEOUT_MS);
        config.put("offsets.topic.replication.factor", "1");
        config.put(ACKS_CONFIG, "all");

        addGenericSettings(config, ssl);

        return config;
    }

    public static Properties consumerProperties(boolean ssl) {
        Properties config = new Properties();

        config.put(ConsumerConfig.CLIENT_ID_CONFIG, localHostname());
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "example-" + (ssl ? "ssl" : "no-ssl"));

        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, IntegerDeserializer.class.getName());
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());

        addGenericSettings(config, ssl);

        return config;
    }

    private static void addGenericSettings(Properties config, boolean ssl) {
        config.put(BOOTSTRAP_SERVERS_CONFIG, "localhost:" + (ssl ? SSL_PORT : PLAIN_PORT));

        if(ssl) {
            config.put(SECURITY_PROTOCOL_CONFIG, "SSL");
            config.put(SSL_TRUSTSTORE_LOCATION_CONFIG, CLIENT_TRUSTSTORE);
            config.put(SSL_TRUSTSTORE_PASSWORD_CONFIG,  PASSWORD);

            config.put(SSL_KEYSTORE_LOCATION_CONFIG, CLIENT_KEYSTORE);
            config.put(SSL_KEYSTORE_PASSWORD_CONFIG, PASSWORD);
            config.put(SSL_KEY_PASSWORD_CONFIG, PASSWORD);
        }
    }

    private static String jksFile(String fileName) {
        var file = new File(fileName);

        if(!file.exists()) {
            throw new IllegalArgumentException("No file with name " + file.getAbsolutePath());
        }

        return file.getAbsolutePath();
    }

    private static final String localHostname() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }
}
