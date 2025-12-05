package com.niraj.restaurant.dto;

import java.math.BigDecimal;

public record ExternalRestaurantDto(
        String externalId,
        String name,
        String address,
        BigDecimal latitude,
        BigDecimal longitude,
        Short priceRange,
        BigDecimal avgRating,
        String slug,
        String hash,
        Long cityId
) {}

