package com.lox.authservice.kafka;

public enum EventType {
    USER_CREATED("user-events"),
    USER_AUTHENTICATED("auth-events"),
    LOGIN_FAILED("auth-events");

    private final String topic;

    EventType(String topic) {
        this.topic = topic;
    }

    public String getTopic() {
        return topic;
    }
}
