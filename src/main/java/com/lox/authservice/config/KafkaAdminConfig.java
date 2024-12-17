// src/main/java/com/lox/authservice/config/KafkaAdminConfig.java
package com.lox.authservice.config;

import com.lox.authservice.kafka.KafkaTopics;
import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;

@Configuration
public class KafkaAdminConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        return new KafkaAdmin(configs);
    }

    @Bean
    public NewTopic authEventsTopic() {
        return TopicBuilder.name(KafkaTopics.AUTH_EVENTS_TOPIC)
                .partitions(1)
                .replicas(1)
                .config("cleanup.policy", "delete")
                .build();
    }

    @Bean
    public NewTopic userEventsTopic() {
        return TopicBuilder.name(KafkaTopics.USER_EVENTS_TOPIC)
                .partitions(1)
                .replicas(1)
                .config("cleanup.policy", "delete")
                .build();
    }
}
