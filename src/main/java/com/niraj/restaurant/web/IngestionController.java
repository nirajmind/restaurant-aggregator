package com.niraj.restaurant.web;

import com.niraj.restaurant.dto.ExternalRestaurantDto;
import com.niraj.restaurant.dto.UpsertResult;
import com.niraj.restaurant.ingestion.IngestionService;
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

}

