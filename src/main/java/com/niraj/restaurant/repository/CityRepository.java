package com.niraj.restaurant.repository;

import com.niraj.restaurant.domain.City;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CityRepository extends JpaRepository<City, Long> {}

