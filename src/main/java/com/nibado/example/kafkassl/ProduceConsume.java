package com.nibado.example.kafkassl;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.time.Duration;
import java.util.List;

public class ProduceConsume {
    private static final int PRODUCE_INTERVAL = 5000;
    private final Producer<Integer, String> producer;
    private final Consumer<Integer, String> consumer;

    private boolean running;
    private final Thread producingThread;
    private final Thread consumingThread;

    public ProduceConsume(Producer<Integer, String> producer, Consumer<Integer, String> consumer) {
        this.producer = producer;
        this.consumer = consumer;
        this.producingThread = new Thread(this::produceProcess, "Producer Thread");
        this.consumingThread = new Thread(this::consumeProcess, "Consumer Thread");
    }

    private void produce(int message) {
        var record = new ProducerRecord<>(Config.TOPIC, message, "Hello World: " + message);
        try {
            producer.send(record).get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        System.out.printf("Produced message %s\n", message);
    }

    private void produceProcess() {
        var counter = 0;
        while (running) {
            produce(++counter);
            try {
                Thread.sleep(PRODUCE_INTERVAL);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        System.out.println("Producing stopped");
    }

    private void consumeProcess() {
        System.out.println("Subscribing to topic " + Config.TOPIC);
        consumer.subscribe(List.of(Config.TOPIC));
        while (running) {
            System.out.println("Polling for message");
            var records = consumer.poll(Duration.ofSeconds(5));

            records.forEach(record -> {
                System.out.printf("%s: %s\n", record.key(), record.value());
            });

            consumer.commitSync();
        }
        System.out.println("Consuming stopped");
    }

    public void start() {
        System.out.println("Starting...");
        running = true;
        producingThread.start();
        consumingThread.start();
    }

    public void stop() {
        running = false;

        try {
            producer.close();
        } catch (Exception e) {
        }
        try {
            consumer.close();
        } catch (Exception e) {
        }

        try {
            producingThread.join();
        } catch (Exception e) {
        }
        try {
            consumingThread.join();
        } catch (Exception e) {
        }
    }
}
