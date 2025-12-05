package com.niraj.restaurant.dto;

import java.math.BigDecimal;
import java.util.Set;

public record RestaurantSearchRequest(
        Long cityId,
        Set<Long> cuisineIds,
        Set<Long> tagIds,
        Integer minPrice,
        Integer maxPrice,
        BigDecimal minLat,
        BigDecimal maxLat,
        BigDecimal minLon,
        BigDecimal maxLon,
        String sort,
        Integer page,
        Integer size
) {}



