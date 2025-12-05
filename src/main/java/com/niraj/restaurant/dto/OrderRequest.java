package com.niraj.restaurant.dto;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public record OrderRequest(
        @NotNull(message = "User ID is required")
        Long userId,

        @NotNull(message = "Restaurant ID is required")
        Long restaurantId,

        @NotNull(message = "Order items cannot be null")
        List<OrderItemRequest> items
) {
}
