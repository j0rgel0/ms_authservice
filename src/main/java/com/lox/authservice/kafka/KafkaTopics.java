package com.lox.authservice.kafka;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class KafkaTopics {

    public static final String USER_EVENTS_TOPIC = "user-events";
    public static final String AUTH_EVENTS_TOPIC = "auth-events";

}
