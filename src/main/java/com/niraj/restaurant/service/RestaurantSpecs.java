package com.niraj.restaurant.service;

import com.niraj.restaurant.domain.Restaurant;
import com.niraj.restaurant.dto.RestaurantSearchRequest;
import jakarta.persistence.criteria.*;
import lombok.NonNull;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public final class RestaurantSpecs {

    private RestaurantSpecs(){}

    public static Specification<@NonNull Restaurant> byRequest(RestaurantSearchRequest req) {
        return (root, query, cb) -> {
            List<Predicate> p = new ArrayList<>();

            // 1. Basic Filters
            addLocationFilters(req, root, cb, p);

            // 2. Relations (Joins)
            addCuisineFilter(req, root, query, p);
            addTagFilter(req, root, query, p);

            // 3. Price Range
            addPriceFilters(req, root, cb, p);

            return cb.and(p.toArray(new Predicate[0]));
        };
    }

    private static void addLocationFilters(RestaurantSearchRequest req, Root<Restaurant> root, CriteriaBuilder cb, List<Predicate> p) {
        if (req.cityId() != null) {
            p.add(cb.equal(root.get("city").get("id"), req.cityId()));
        }
        // Simple bounding box logic for lat/lon (if provided)
        if (req.minLat() != null && req.maxLat() != null) {
            p.add(cb.between(root.get("latitude"), req.minLat(), req.maxLat()));
        }
        if (req.minLon() != null && req.maxLon() != null) {
            p.add(cb.between(root.get("longitude"), req.minLon(), req.maxLon()));
        }
    }

    private static void addCuisineFilter(RestaurantSearchRequest req, Root<Restaurant> root, CriteriaQuery<?> q, List<Predicate> p) {
        if (req.cuisineIds() != null && !req.cuisineIds().isEmpty()) {
            // Join is necessary to filter by child collection
            Join<Object, Object> cuisines = root.join("cuisines", JoinType.INNER);
            p.add(cuisines.get("id").in(req.cuisineIds()));
            q.distinct(true); // Avoid duplicate results
        }
    }

    private static void addTagFilter(RestaurantSearchRequest req, Root<Restaurant> root, CriteriaQuery<?> q, List<Predicate> p) {
        if (req.tagIds() != null && !req.tagIds().isEmpty()) {
            Join<Object, Object> tags = root.join("tags", JoinType.INNER);
            p.add(tags.get("id").in(req.tagIds()));
            q.distinct(true);
        }
    }

    private static void addPriceFilters(RestaurantSearchRequest req, Root<Restaurant> root, CriteriaBuilder cb, List<Predicate> p) {
        if (req.minPrice() != null) {
            p.add(cb.ge(root.get("priceRange"), req.minPrice()));
        }
        if (req.maxPrice() != null) {
            p.add(cb.le(root.get("priceRange"), req.maxPrice()));
        }
    }
}


