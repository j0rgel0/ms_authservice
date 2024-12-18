package com.lox.authservice.api.kafka.events;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderRecord;

@Component
@RequiredArgsConstructor
@Slf4j
public class EventProducer {

    private final KafkaSender<String, Object> kafkaSender;

    /**
     * Publishes an event to the topic specified by the EventType reactively.
     *
     * @param eventType The event type that determines the topic.
     * @param event     The event to publish.
     * @param <T>       The type of event implementing the Event interface.
     * @return Mono<Void> Indicates the completion of the send operation.
     */
    public <T extends Event> Mono<Void> publishEvent(EventType eventType, T event) {
        SenderRecord<String, Object, String> senderRecord = SenderRecord.create(
                new org.apache.kafka.clients.producer.ProducerRecord<>(eventType.getTopic(), event),
                eventType.getTopic() // correlation metadata, can be any unique identifier
        );

        return kafkaSender.send(Mono.just(senderRecord))
                .doOnNext(result -> {
                    log.info("Successfully published event to {}: {} with offset [{}]",
                            eventType.getTopic(), event, result.recordMetadata().offset());
                })
                .doOnError(error -> {
                    log.error("Failed to publish event to {}: {}", eventType.getTopic(), error.getMessage());
                })
                .then(); // Convert to Mono<Void>
    }
}
