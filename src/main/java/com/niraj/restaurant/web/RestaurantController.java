package com.niraj.restaurant.web;

import com.niraj.restaurant.dto.RestaurantResponse;
import com.niraj.restaurant.dto.RestaurantSearchRequest;
import com.niraj.restaurant.service.AnalyticsProducer;
import com.niraj.restaurant.service.RestaurantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
@RequestMapping("/api/v1/restaurants")
@RequiredArgsConstructor
public class RestaurantController {
    private final RestaurantService service;

    private final AnalyticsProducer analyticsService;

    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'RESTAURANT_OWNER', 'ADMIN')")
    public Page<@NonNull RestaurantResponse> search(@Valid RestaurantSearchRequest req, Pageable pageable) {
        return service.search(req, pageable);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()") // 2. Checks if user is just logged in
    public RestaurantResponse get(@PathVariable Long id) {
        // 1. Log the view (Non-blocking side effect)
        // We assume "Anonymous" if auth is missing, or extract from SecurityContext
        String user = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName();
        analyticsService.recordView(id, user);

        // 2. Return data (Fast)
        return service.getById(id);
    }
}

