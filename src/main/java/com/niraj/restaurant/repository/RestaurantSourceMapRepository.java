package com.niraj.restaurant.repository;

import com.niraj.restaurant.domain.RestaurantSourceMap;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RestaurantSourceMapRepository extends JpaRepository<RestaurantSourceMap, Long> {
    Optional<RestaurantSourceMap> findBySourceIdAndExternalId(Long sourceId, String externalId);
}

