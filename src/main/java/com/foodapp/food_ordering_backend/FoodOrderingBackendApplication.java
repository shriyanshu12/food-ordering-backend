package com.foodapp.food_ordering_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class FoodOrderingBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(FoodOrderingBackendApplication.class, args);
	}

}
