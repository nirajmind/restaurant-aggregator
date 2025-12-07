package com.niraj.restaurant.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class AnalyticsConsumer {

    /**
     * BATCH LISTENER
     * Because we set 'spring.kafka.listener.type: batch',
     * Spring gives us a List<Event> instead of a single Event.
     */
    @KafkaListener(topics = "restaurant-views", groupId = "restaurant-analytics-group")
    public void processBatch(List<AnalyticsProducer.RestaurantViewEvent> events) {
        if (events.isEmpty()) return;

        log.info("KAFKA BATCH: Received {} view events.", events.size());

        // In a real app, you would do a BULK INSERT here (e.g., ClickHouse, Postgres Copy)
        // processing 50 items in 1 DB Transaction is much faster than 50 individual inserts.

        // Example Aggregation:
        long distinctRestaurants = events.stream()
                .map(AnalyticsProducer.RestaurantViewEvent::restaurantId)
                .distinct()
                .count();

        log.info("ANALYTICS: Analyzed traffic for {} unique restaurants.", distinctRestaurants);
    }
}
