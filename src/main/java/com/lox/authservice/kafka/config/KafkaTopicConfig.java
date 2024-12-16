// src/main/java/com/lox/authservice/kafka/config/KafkaTopicConfig.java

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
        String topicName = "auth-events";
        log.info("Creating topic: {}", topicName);
        return new NewTopic(topicName, 1, (short) 1);
    }

    // Puedes definir más tópicos si es necesario
    @Bean
    public NewTopic userEventsTopic() {
        String topicName = "user-events";
        log.info("Creating topic: {}", topicName);
        return new NewTopic(topicName, 1, (short) 1);
    }
}
