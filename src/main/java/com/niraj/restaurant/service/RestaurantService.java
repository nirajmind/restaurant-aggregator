package com.niraj.restaurant.service;

import com.niraj.restaurant.domain.Restaurant;
import com.niraj.restaurant.dto.RestaurantResponse;
import com.niraj.restaurant.dto.RestaurantSearchRequest;
import com.niraj.restaurant.mapper.RestaurantMapper;
import com.niraj.restaurant.repository.RestaurantRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Comparator;

@Service
@RequiredArgsConstructor
@Slf4j
public class RestaurantService {
    private final RestaurantRepository repo;
    private final RestaurantMapper mapper = new RestaurantMapper();

    public Page<@NonNull RestaurantResponse> search(RestaurantSearchRequest req, Pageable pageable) {
        Specification<@NonNull Restaurant> spec = RestaurantSpecs.byRequest(req);
        Page<@NonNull Restaurant> page = repo.findAll(spec, pageable);
        // optional sort override
        if ("rating".equalsIgnoreCase(req.sort())) {
            page = new PageImpl<>(
                    page.getContent().stream().sorted(Comparator.comparing(Restaurant::getAvgRating, Comparator.nullsLast(Comparator.naturalOrder())).reversed()).toList(),
                    pageable, page.getTotalElements()
            );
        }
        return page.map(mapper::toDto);
    }

    @Cacheable(value = "restaurants", key = "#id")
    public RestaurantResponse getById(Long id) {

        log.info("Fetching Restaurant {} from Database...", id);

        Restaurant r = repo.findById(id).orElseThrow(() -> new RuntimeException("Restaurant not found"));
        return mapper.toDto(r);
    }

}

