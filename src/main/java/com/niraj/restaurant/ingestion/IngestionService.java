package com.niraj.restaurant.ingestion;

import com.niraj.restaurant.domain.Restaurant;
import com.niraj.restaurant.domain.RestaurantSourceMap;
import com.niraj.restaurant.domain.Source;
import com.niraj.restaurant.dto.ExternalRestaurantDto;
import com.niraj.restaurant.dto.UpsertResult;
import com.niraj.restaurant.repository.CityRepository;
import com.niraj.restaurant.repository.RestaurantRepository;
import com.niraj.restaurant.repository.RestaurantSourceMapRepository;
import com.niraj.restaurant.repository.SourceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class IngestionService {
    private final SourceRepository sourceRepo;
    private final RestaurantRepository restaurantRepo;
    private final RestaurantSourceMapRepository mapRepo;
    private final CityRepository cityRepo;
    private final DedupService dedup;

    public IngestionService(SourceRepository sourceRepo, RestaurantRepository restaurantRepo,
                            RestaurantSourceMapRepository mapRepo, CityRepository cityRepo, DedupService dedup) {
        this.sourceRepo = sourceRepo; this.restaurantRepo = restaurantRepo; this.mapRepo = mapRepo; this.cityRepo = cityRepo; this.dedup = dedup;
    }

    @Transactional
    public UpsertResult upsertRestaurants(String sourceName, List<ExternalRestaurantDto> batch) {
        Source s = sourceRepo.findByName(sourceName).orElseGet(() -> {
            Source ns = new Source(); ns.setName(sourceName); ns.setEnabled(true); return sourceRepo.save(ns);
        });
        int created = 0, updated = 0;
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

