package com.niraj.restaurant.repository;

import com.niraj.restaurant.domain.Cuisine;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CuisineRepository extends JpaRepository<Cuisine, Long> {}

