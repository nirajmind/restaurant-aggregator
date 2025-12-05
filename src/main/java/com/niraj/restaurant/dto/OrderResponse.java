package com.niraj.restaurant.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderResponse(
        Long orderId,
        String status,
        BigDecimal totalAmount,
        LocalDateTime createdAt,
        String message
) {
}
