// src/main/java/com/lox/authservice/kafka/Producer.java

package com.lox.authservice.kafka;

import com.lox.authservice.models.events.AuthEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderRecord;
import reactor.kafka.sender.SenderResult;

@Component
@RequiredArgsConstructor
@Slf4j
public class Producer {

    private final KafkaSender<String, AuthEvent> kafkaSender;

    private static final String TOPIC = "auth-events";

    public Mono<Void> publishAuthEvent(AuthEvent event) {
        // Crear un ProducerRecord
        ProducerRecord<String, AuthEvent> producerRecord = new ProducerRecord<>(TOPIC,
                event.getUsername(), event);

        // Crear un SenderRecord con ProducerRecord y metadatos de correlación de tipo String
        SenderRecord<String, AuthEvent, String> senderRecord = SenderRecord.create(producerRecord,
                event.getEventType());

        // Enviar el SenderRecord a Kafka
        return kafkaSender.send(Mono.just(senderRecord))
                .doOnNext(this::handleResult)
                .then();
    }

    private void handleResult(SenderResult<String> result) {
        if (result.exception() != null) {
            log.error("Error sending message to Kafka: ", result.exception());
        } else {
            log.debug("Message sent successfully to partition {}, offset {}",
                    result.recordMetadata().partition(),
                    result.recordMetadata().offset());
            log.debug("Correlation Metadata (event type): {}", result.correlationMetadata());
        }
    }
}
