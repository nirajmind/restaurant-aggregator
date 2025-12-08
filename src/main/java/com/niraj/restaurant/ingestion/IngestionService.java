package com.niraj.restaurant.ingestion;

import com.niraj.restaurant.domain.City;
import com.niraj.restaurant.domain.Restaurant;
import com.niraj.restaurant.domain.RestaurantSourceMap;
import com.niraj.restaurant.domain.Source;
import com.niraj.restaurant.dto.ExternalRestaurantDto;
import com.niraj.restaurant.dto.UpsertResult;
import com.niraj.restaurant.repository.CityRepository;
import com.niraj.restaurant.repository.RestaurantRepository;
import com.niraj.restaurant.repository.RestaurantSourceMapRepository;
import com.niraj.restaurant.repository.SourceRepository;
import com.niraj.restaurant.service.IngestionProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
@RequiredArgsConstructor
public class IngestionService {
    private final SourceRepository sourceRepo;
    private final RestaurantRepository restaurantRepo;
    private final RestaurantSourceMapRepository mapRepo;
    private final CityRepository cityRepo;
    private final DedupService dedup;
    // Inject the adapter (This is the bean with @CircuitBreaker)
    private final SourceAdapter sourceAdapter;
    private final IngestionProducer producer;

    // --- 1. THE MISSING ASYNC SYNC METHOD ---
    @Async("taskExecutor")
    public CompletableFuture<String> triggerSync() {
        log.info("ASYNC START: Sync job started on thread: {}", Thread.currentThread().getName());

        try {
            // We fetch a dummy city for this test to trigger the adapter
            City dubai = cityRepo.findAll().stream().findFirst().orElse(null);

            if (dubai == null) {
                log.warn("No cities found to sync.");
                return CompletableFuture.completedFuture("No Cities");
            }

            // CALL THE CIRCUIT BREAKER PROTECTED METHOD
            var restaurants = sourceAdapter.fetchRestaurants(dubai);

            // 2. Stream to Kafka (Producer)
            // We iterate the stream and fire events.
            // This is non-blocking IO for the application logic.
            long count = restaurants.peek(producer::sendRestaurant).count();
            log.info("SYNC COMPLETE: Pushed {} events to Kafka.", count);
            return CompletableFuture.completedFuture("Pushed " + count);
        } catch (Exception e) {
            log.error("ASYNC ERROR: Sync failed due to: {}", e.getMessage());
            return CompletableFuture.completedFuture("Sync Failed");
        }
    }

    @Transactional
    public UpsertResult upsertRestaurants(String sourceName, List<ExternalRestaurantDto> batch) {
        Source s = sourceRepo.findByName(sourceName).orElseGet(() -> {
            Source ns = new Source(); ns.setName(sourceName); ns.setEnabled(true); return sourceRepo.save(ns);
        });
        int created = 0;
        int updated = 0;
        for (ExternalRestaurantDto ext : batch) {
            Optional<RestaurantSourceMap> existingMap = mapRepo.findBySourceIdAndExternalId(s.getId(), ext.externalId());
            Restaurant canonical = existingMap.map(RestaurantSourceMap::getRestaurant).orElseGet(() -> dedup.findMatchOrCreate(ext));

            if (ext.cityId() != null) {
                cityRepo.findById(ext.cityId()).ifPresent(canonical::setCity);
            }

            // apply updates
            canonical.setAddress(ext.address());
            canonical.setLatitude(ext.latitude());
            canonical.setLongitude(ext.longitude());
            canonical.setPriceRange(ext.priceRange());
            canonical.setAvgRating(ext.avgRating());

            restaurantRepo.save(canonical);
            upsertMap(canonical, s, ext);

            if (existingMap.isEmpty()) created++; else updated++;
        }
        return new UpsertResult(created, updated);
    }

    private void upsertMap(Restaurant r, Source s, ExternalRestaurantDto ext) {
        mapRepo.findBySourceIdAndExternalId(s.getId(), ext.externalId())
                .ifPresentOrElse(m -> { m.setExternalSlug(ext.slug()); m.setRawHash(ext.hash()); },
                        () -> mapRepo.save(new RestaurantSourceMap(r, s, ext.externalId(), ext.slug(), ext.hash())));
    }
}

