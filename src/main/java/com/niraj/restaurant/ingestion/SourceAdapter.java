package com.niraj.restaurant.ingestion;

import com.niraj.restaurant.domain.City;
import com.niraj.restaurant.dto.ExternalRestaurantDto;

import java.util.stream.Stream;

public interface SourceAdapter {
    String name();
    Stream<ExternalRestaurantDto> fetchRestaurants(City city);
}

