package com.niraj.restaurant.repository;

import com.niraj.restaurant.domain.Source;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SourceRepository extends JpaRepository<@NonNull Source, @NonNull Long> {
    Optional<Source> findByName(String name);
}

