package com.niraj.restaurant.repository;

import com.niraj.restaurant.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    // Custom queries can go here (e.g., findByUserId)
}
