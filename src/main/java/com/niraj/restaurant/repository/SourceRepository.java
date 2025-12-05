package com.niraj.restaurant.repository;

import com.niraj.restaurant.domain.Source;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SourceRepository extends JpaRepository<Source, Long> {
    Optional<Source> findByName(String name);
}

