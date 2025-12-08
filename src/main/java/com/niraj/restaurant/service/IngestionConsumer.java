package com.niraj.restaurant.service;

import com.niraj.restaurant.dto.ExternalRestaurantDto;
import com.niraj.restaurant.dto.UpsertResult;
import com.niraj.restaurant.ingestion.IngestionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class IngestionConsumer {

    private final IngestionService ingestionService;

    /**
     * BATCH LISTENER
     * Kafka accumulates messages and hands them to us in a List.
     * This turns 50 network calls into 1 efficient DB Transaction.
     */
    @KafkaListener(topics = "raw-restaurants", groupId = "restaurant-ingestion-group",
            containerFactory = "kafkaListenerContainerFactory")
    public void consumeBatch(List<ExternalRestaurantDto> batch) {
        log.info("KAFKA WORKER: Received batch of {} restaurants to process.", batch.size());

        // Delegate to the transactional service
        UpsertResult result = ingestionService.upsertRestaurants("KafkaSource", batch);

        log.info("KAFKA WORKER: Batch processed. Created: {}, Updated: {}", result.created(), result.updated());
    }
}
