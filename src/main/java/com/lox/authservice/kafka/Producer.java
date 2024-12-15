package com.lox.authservice.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.lox.authservice.models.Analytic;
import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class Producer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private static final String TOPIC = "auth-events";

    public void publishAuthDatum(String username, String description) throws JsonProcessingException {
        Analytic analytic = new Analytic();
        analytic.setObjectid(UUID.randomUUID().toString());
        analytic.setPrincipal(username);
        analytic.setType("AUTH");
        analytic.setDescription(description);
        analytic.setTimestamp(Instant.now());

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule()); // Register JavaTimeModule
        String message = mapper.writeValueAsString(analytic);
        log.info("Publishing message: {}", message);
        kafkaTemplate.send(TOPIC, message);
    }

}
