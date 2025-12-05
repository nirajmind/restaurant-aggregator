package com.niraj.restaurant.dto;

import java.math.BigDecimal;
import java.util.Set;

public record RestaurantResponse(
        Long id, String name, String address, BigDecimal lat, BigDecimal lon,
        Short priceRange, BigDecimal avgRating, Set<String> cuisines, Set<String> tags
) {}

