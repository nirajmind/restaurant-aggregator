package com.niraj.restaurant.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class NotificationService {

    /**
     * This method runs in a completely separate thread.
     * The Controller does NOT wait for this to finish.
     */
    @Async("taskExecutor")
    public CompletableFuture<String> sendOrderConfirmation(Long orderId) {
        long startTime = System.currentTimeMillis();
        String threadName = Thread.currentThread().getName();

        log.info("START: Sending email for Order ID: {} - Thread: {}", orderId, threadName);

        try {
            // Simulate a slow network call (e.g., SMTP server or 3rd Party API)
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        long endTime = System.currentTimeMillis();
        log.info("COMPLETED: Email sent for Order ID: {} in {}ms - Thread: {}",
                orderId, (endTime - startTime), threadName);

        return CompletableFuture.completedFuture("Email Sent");
    }
}
