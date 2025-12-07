package com.niraj.restaurant.service;

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
public class AnalyticsProducer {

    private final KafkaTemplate<@NonNull String, @NonNull Object> kafkaTemplate;
    private static final String TOPIC = "restaurant-views";

    /**
     * Fire and Forget.
     * The Kafka Client (internal to Spring) has its own memory buffer.
     * It batches messages and sends them over the network efficiently.
     */
    public void recordView(Long restaurantId, String username) {
        RestaurantViewEvent event = new RestaurantViewEvent(restaurantId, username, LocalDateTime.now());

        // Key = restaurantId (Ensures all views for Restaurant #1 go to the same Partition)
        kafkaTemplate.send(TOPIC, String.valueOf(restaurantId), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("KAFKA FAIL: Could not send view event", ex);
                    }
                });
    }

    // Must be Serializable for JSON conversion
    public record RestaurantViewEvent(Long restaurantId, String username, LocalDateTime timestamp) implements Serializable {}
}
