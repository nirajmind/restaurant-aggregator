package com.niraj.restaurant.service;

import com.niraj.restaurant.dto.ExternalRestaurantDto;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class IngestionProducer {

    private final KafkaTemplate<@NonNull String, @NonNull Object> kafkaTemplate;
    private static final String TOPIC = "restaurant-views";

    public void sendRestaurant(ExternalRestaurantDto dto) {
        // Key = externalId (Ensures updates for the same restaurant go to the same partition/consumer)
        kafkaTemplate.send(TOPIC, dto.externalId(), dto)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("KAFKA: Failed to send restaurant {}", dto.name(), ex);
                    }
                });
    }
}
