package com.niraj.restaurant;

import com.niraj.restaurant.domain.Order;
import com.niraj.restaurant.domain.OrderStatus;
import com.niraj.restaurant.dto.OrderRequest;
import com.niraj.restaurant.dto.OrderResponse;
import com.niraj.restaurant.repository.OrderRepository;
import com.niraj.restaurant.service.NotificationService;
import com.niraj.restaurant.service.OrderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;

@ExtendWith(MockitoExtension.class) // 1. Enable Mockito
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private OrderService orderService; // 2. Inject Mocks into Service

    @Test
    void placeOrder_ShouldSaveOrderAndTriggerNotification() {
        // Arrange (Given)
        OrderRequest request = new OrderRequest(1L, 100L, List.of());

        Order mockedSavedOrder = Order.builder()
                .id(55L)
                .userId(1L)
                .restaurantId(100L)
                .status(OrderStatus.PENDING)
                .totalAmount(BigDecimal.valueOf(99.99))
                .build();

        // When repository.save() is called, return our mock object
        when(orderRepository.save(any(Order.class))).thenReturn(mockedSavedOrder);

        // Mock the async service to avoid null pointer
        when(notificationService.sendOrderConfirmation(anyLong()))
                .thenReturn(CompletableFuture.completedFuture("Email Sent"));

        // Act (When)
        OrderResponse response = orderService.placeOrder(request);

        // Assert (Then)
        assertNotNull(response);
        assertEquals(55L, response.orderId());
        assertEquals("PENDING", response.status());

        // Verify that the "Side Effect" (Notification) actually happened
        verify(notificationService, times(1)).sendOrderConfirmation(55L);
        verify(orderRepository, times(1)).save(any(Order.class));
    }
}
