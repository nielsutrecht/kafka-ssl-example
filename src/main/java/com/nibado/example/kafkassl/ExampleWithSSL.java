package com.nibado.example.kafkassl;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;

public class ExampleWithSSL {
    public static void main(String[] args) throws Exception {
        var producer = new KafkaProducer<Integer, String>(Config.producerProperties(true));
        var consumer = new KafkaConsumer<Integer, String>(Config.consumerProperties(true));
        var example = new ProduceConsume(producer, consumer);

        example.start();

        System.in.read();
        System.out.println("Shutting down...");
        example.stop();
        System.out.println("Finished!");
    }
}
