package com.niraj.restaurant.web;

import com.niraj.restaurant.dto.ExternalRestaurantDto;
import com.niraj.restaurant.dto.UpsertResult;
import com.niraj.restaurant.ingestion.IngestionService;
import org.jspecify.annotations.NonNull;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/ingestion")
public class IngestionController {
    private final IngestionService service;
    public IngestionController(IngestionService service) { this.service = service; }

    @PostMapping("/{source}/restaurants:upsert")
    public UpsertResult upsertRestaurants(@PathVariable String source, @RequestBody List<ExternalRestaurantDto> batch) {
        return service.upsertRestaurants(source, batch);
    }

    // Add this to IngestionController.java
    @PostMapping("/sync")
    public org.springframework.http.ResponseEntity<@NonNull String> startSync() {
        // You need to expose 'triggerSync' in IngestionService for this
        service.triggerSync();
        return org.springframework.http.ResponseEntity.accepted().body("Sync started.");
    }

}

