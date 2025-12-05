package com.niraj.restaurant.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync // 1. Switches on Spring's background processing features
public class AsyncConfig {

    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        // 2. Core settings for High Performance
        executor.setCorePoolSize(5);     // Always keep 5 threads alive
        executor.setMaxPoolSize(10);     // Burst up to 10 if load is high
        executor.setQueueCapacity(500);  // Queue 500 tasks before rejecting
        executor.setThreadNamePrefix("RestAsync-"); // Helps you see it in logs

        executor.initialize();
        return executor;
    }
}
