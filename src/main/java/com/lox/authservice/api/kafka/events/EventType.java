package com.lox.authservice.api.kafka.events;

import com.lox.authservice.api.kafka.topics.KafkaTopics;
import lombok.Getter;

@Getter
public enum EventType {
    USER_CREATED(KafkaTopics.USER_EVENTS_TOPIC),
    USER_AUTHENTICATED(KafkaTopics.AUTH_EVENTS_TOPIC),
    LOGIN_FAILED(KafkaTopics.AUTH_EVENTS_TOPIC);

    private final String topic;

    EventType(String topic) {
        this.topic = topic;
    }

}
