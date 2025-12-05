package com.niraj.restaurant.web;

import com.niraj.restaurant.dto.RestaurantResponse;
import com.niraj.restaurant.dto.RestaurantSearchRequest;
import com.niraj.restaurant.service.RestaurantService;
import jakarta.validation.Valid;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/restaurants")
public class RestaurantController {
    private final RestaurantService service;
    public RestaurantController(RestaurantService service) { this.service = service; }

    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'RESTAURANT_OWNER', 'ADMIN')")
    public Page<@NonNull RestaurantResponse> search(@Valid RestaurantSearchRequest req, Pageable pageable) {
        return service.search(req, pageable);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()") // 2. Checks if user is just logged in
    public RestaurantResponse get(@PathVariable Long id) {
        return service.getById(id);
    }
}

