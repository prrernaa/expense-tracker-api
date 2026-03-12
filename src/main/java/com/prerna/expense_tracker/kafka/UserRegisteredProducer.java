package com.prerna.expense_tracker.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserRegisteredProducer {

    private static final String TOPIC = "user.registered";
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void publishUserRegisteredEvent(UserRegisteredEvent event) {
        try {
            String message = objectMapper.writeValueAsString(event);
            log.info("Publishing event to Kafka topic {}: {}", TOPIC, message);
            kafkaTemplate.send(TOPIC, event.getUserId(), message);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize event: {}", e.getMessage());
        }
    }
}