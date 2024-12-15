package com.lox.authservice.kafka.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class KafkaTopicConfig {

    @Bean
    public NewTopic authEventsTopic() {
        log.info("Creating topic: auth-events");
        NewTopic topic = new NewTopic("auth-events", 1, (short) 1);
        log.info("Topic created: {}", topic.name());
        return topic;
    }

    @Bean
    public NewTopic anotherTopic() {
        log.info("Creating topic: another-topic");
        NewTopic topic = new NewTopic("another-topic", 2, (short) 1);
        log.info("Topic created: {}", topic.name());
        return topic;
    }
}
