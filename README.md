# Kafka SSL example

Example on how to use two-way SSL / Mutual TLS with Kafka and Java.

Based on the [confluent documentation](https://docs.confluent.io/platform/current/kafka/authentication_ssl.html).

## Running

Docker-compose files from [here](https://github.com/wurstmeister/kafka-docker).

You can start Kafka + Zookeeper locally with docker-compose: 

    docker-compose up
    
## Test connectivity

    openssl s_client -connect localhost:9093.
