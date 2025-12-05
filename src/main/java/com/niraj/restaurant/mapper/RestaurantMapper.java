package com.niraj.restaurant.mapper;

import com.niraj.restaurant.domain.Restaurant;
import com.niraj.restaurant.dto.RestaurantResponse;

import java.util.stream.Collectors;

public class RestaurantMapper {
    public RestaurantResponse toDto(Restaurant r) {
        return new RestaurantResponse(
                r.getId(),
                r.getCanonicalName(),
                r.getAddress(),
                r.getLatitude(),
                r.getLongitude(),
                r.getPriceRange(),
                r.getAvgRating(),
                r.getCuisines().stream().map(c -> c.getName()).collect(Collectors.toSet()),
                r.getTags().stream().map(t -> t.getName()).collect(Collectors.toSet())
        );
    }
}

