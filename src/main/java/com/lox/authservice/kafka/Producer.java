package com.lox.authservice.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lox.authservice.models.events.Event;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class Producer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    /**
     * Publishes an event to the topic specified by the EventType asynchronously.
     *
     * @param eventType The event type that determines the topic.
     * @param event     The event to publish.
     * @param <T>       The type of event implementing the Event interface.
     * @return CompletableFuture<Void> Indicates the completion of the send operation.
     */
    public <T extends Event> CompletableFuture<Void> publishEvent(EventType eventType, T event) {
        try {
            String eventJson = objectMapper.writeValueAsString(event);

            // Send message using KafkaTemplate and handle success/failure
            CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(
                    eventType.getTopic(), eventJson);

            // Handle result using `whenComplete`
            return future.whenComplete((result, throwable) -> {
                if (throwable != null) {
                    // Handle failure
                    log.error("Failed to publish event to {}: {}", eventType.getTopic(),
                            throwable.getMessage());
                } else {
                    // Handle success
                    log.info("Successfully published event to {}: {} with offset [{}]",
                            eventType.getTopic(), eventJson, result.getRecordMetadata().offset());
                }
            }).thenApply(result -> null); // Return CompletableFuture<Void>
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize event: {}", e.getMessage());
            CompletableFuture<Void> failedFuture = new CompletableFuture<>();
            failedFuture.completeExceptionally(e);
            return failedFuture;
        }
    }
}
