package com.niraj.restaurant.ingestion;

import com.niraj.restaurant.domain.City;
import com.niraj.restaurant.dto.ExternalRestaurantDto;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.stream.Stream;

@Component
@Slf4j
public class MockSourceAdapter implements SourceAdapter {

    @Override
    public String name() {
        return "MockSource";
    }

    @Override
    @CircuitBreaker(name = "externalSource", fallbackMethod = "fallbackFetch")
    public Stream<ExternalRestaurantDto> fetchRestaurants(City city) {
        log.info("ATTEMPT: Fetching from external source for city: {}", city.getName());

        // Simulation: Fail 50% of the time to test Circuit Breaker
        if (Math.random() > 0.5) {
            throw new RuntimeException("External API Down!");
        }

        // Return mock data
        return Stream.of(
                new ExternalRestaurantDto(
                        "ext-1", "Mock Burger", "123 St", BigDecimal.valueOf(25.0), BigDecimal.valueOf(55.0), (short) 2, BigDecimal.valueOf(4.5), String.valueOf(city.getId()), "mock-burger", 1L
                )
        );
    }

    public Stream<ExternalRestaurantDto> fallbackFetch(City city, Exception e) {
        log.warn("FALLBACK: Returning empty stream due to: {}", e.getMessage());
        return Stream.empty();
    }
}
