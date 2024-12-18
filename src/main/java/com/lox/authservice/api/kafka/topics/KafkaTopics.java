package com.lox.authservice.api.kafka.topics;

import com.lox.authservice.common.kafka.KafkaConfig;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class KafkaTopics {

    public static final String USER_EVENTS_TOPIC = "user-events";
    public static final String AUTH_EVENTS_TOPIC = "auth-events";

    private final KafkaConfig kafkaConfig;

    @Bean
    public NewTopic userEventsTopic() {
        return kafkaConfig.createTopic(USER_EVENTS_TOPIC, 1, (short) 1);
    }

    @Bean
    public NewTopic authEventsTopic() {
        return kafkaConfig.createTopic(AUTH_EVENTS_TOPIC, 1, (short) 1);
    }

}
