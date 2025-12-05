package com.niraj.restaurant.service;

import com.niraj.restaurant.domain.Order;
import com.niraj.restaurant.domain.OrderStatus;
import com.niraj.restaurant.dto.OrderRequest;
import com.niraj.restaurant.dto.OrderResponse;
import com.niraj.restaurant.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final NotificationService notificationService; // Injecting our Async Service

    private static void accept(String result) {
        // This block runs in the background thread (RestAsync-1) AFTER the email is sent
        log.info("Async Callback Success: {}", result);
    }

    @Transactional
    public OrderResponse placeOrder(OrderRequest request) {
        log.info("Processing order for User: {}", request.userId());

        // 1. Logic: In a real app, calculate total from MenuItems
        // For this demo, we mock the calculation
        BigDecimal calculatedTotal = BigDecimal.valueOf(99.99);

        // 2. Create & Save Order (Synchronous - Blocks HTTP request)
        Order newOrder = Order.builder()
                .userId(request.userId())
                .restaurantId(request.restaurantId())
                .totalAmount(calculatedTotal)
                .status(OrderStatus.PENDING)
                .build();

        Order savedOrder = orderRepository.save(newOrder);

        // 3. Trigger Notification (Asynchronous - Non-blocking)
        // This method returns immediately, allowing the HTTP response to go back to the user
        // while the "email" is sent in the background thread.
        CompletableFuture<String> emailFuture = notificationService.sendOrderConfirmation(savedOrder.getId());

        emailFuture.thenAccept(OrderService::accept).exceptionally(ex -> {
            // This runs if the async method throws an exception
            log.error("Async Callback Failed: Could not send email for Order {}", savedOrder.getId(), ex);
            return null; // Return null to handle the void return expectation of exceptionally
        });

        log.info("Order saved with ID: {}. Returning response.", savedOrder.getId());

        // 4. Return DTO
        return new OrderResponse(
                savedOrder.getId(),
                savedOrder.getStatus().name(),
                savedOrder.getTotalAmount(),
                savedOrder.getCreatedAt(),
                "Order Placed Successfully. Confirmation email sent."
        );
    }
}