package com.transaction.challenge.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopics {
    public static final String TRANSACTIONS_TOPIC = "transactions";

    @Bean
    public NewTopic transactionsTopic() {
        return TopicBuilder
        .name(TRANSACTIONS_TOPIC)
        .partitions(4)
        .replicas(1)
        .build();
    }
}