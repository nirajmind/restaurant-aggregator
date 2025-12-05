package com.niraj.restaurant.repository;

import com.niraj.restaurant.domain.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag, Long> {}

