package com.niraj.restaurant.dto;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Set;

public record RestaurantResponse(
        Long id, String name, String address, BigDecimal lat, BigDecimal lon,
        Short priceRange, BigDecimal avgRating, Set<String> cuisines, Set<String> tags
) implements Serializable{
    @Serial
    private static final long serialVersionUID = 1L;
}

