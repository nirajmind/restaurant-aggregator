package com.niraj.restaurant.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Service
@Slf4j
public class AnalyticsService {

    // 1. The Non-Blocking Buffer
    // High write throughput, zero locking contention.
    private final Queue<RestaurantViewEvent> eventBuffer = new ConcurrentLinkedQueue<>();

    /**
     * Called by the Controller on every GET request.
     * This method is extremely fast (nanoseconds) because it relies on CAS (Compare-And-Swap), not Locks.
     */
    public void recordView(Long restaurantId, String username) {
        eventBuffer.offer(new RestaurantViewEvent(restaurantId, username, LocalDateTime.now()));
    }

    /**
     * 2. The Consumer (Batch Processor)
     * Runs every 5 seconds to drain the queue and process events in bulk.
     * This turns 1000 database inserts into 1 batch operation.
     */
    @Scheduled(fixedRate = 5000)
    public void flushEvents() {
        if (eventBuffer.isEmpty()) return;

        List<RestaurantViewEvent> batch = new ArrayList<>();
        RestaurantViewEvent event;

        // Drain the queue (Max 100 items per run to prevent memory spikes)
        int processed = 0;
        while ((event = eventBuffer.poll()) != null && processed < 100) {
            batch.add(event);
            processed++;
        }

        if (!batch.isEmpty()) {
            processBatch(batch);
        }
    }

    private void processBatch(List<RestaurantViewEvent> batch) {
        // In a real app, this would be a "repository.saveAll(batch)" or sending to Kafka.
        // For this demo, we calculate a simple aggregation.
        long uniqueRestaurants = batch.stream().map(RestaurantViewEvent::restaurantId).distinct().count();

        log.info("ANALYTICS FLUSH: Processed {} view events covering {} unique restaurants.",
                batch.size(), uniqueRestaurants);

        // Example: Log specific hot items
        batch.forEach(e -> log.debug("User {} viewed Restaurant {}", e.username(), e.restaurantId()));
    }

    // Immutable Event Record
    public record RestaurantViewEvent(Long restaurantId, String username, LocalDateTime timestamp) {}
}
