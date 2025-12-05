package com.niraj.restaurant.ingestion;

import com.niraj.restaurant.domain.Restaurant;
import com.niraj.restaurant.dto.ExternalRestaurantDto;
import com.niraj.restaurant.repository.RestaurantRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DedupService {
    private final RestaurantRepository restaurantRepo;

    public DedupService(RestaurantRepository restaurantRepo) { this.restaurantRepo = restaurantRepo; }

    public Restaurant findMatchOrCreate(ExternalRestaurantDto ext) {
        String normalized = ext.name().trim().toLowerCase();
        Optional<Restaurant> byName = restaurantRepo.findByCanonicalName(normalized);
        if (byName.isPresent()) return byName.get();
        Restaurant r = new Restaurant();
        r.setCanonicalName(normalized);
        r.setAddress(ext.address());
        r.setLatitude(ext.latitude());
        r.setLongitude(ext.longitude());
        r.setPriceRange(ext.priceRange());
        r.setAvgRating(ext.avgRating());
        return r;
    }

}

