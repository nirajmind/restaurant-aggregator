package com.niraj.restaurant;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class RestaurantAggregatorApplication {

	public static void main(String[] args) {
		SpringApplication.run(RestaurantAggregatorApplication.class, args);
	}

}
