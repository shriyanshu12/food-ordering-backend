package com.foodapp.food_ordering_backend.repository;

import com.foodapp.food_ordering_backend.entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface RestaurantRepository extends
        JpaRepository<Restaurant, Long>,
        JpaSpecificationExecutor<Restaurant> {

    boolean existsByEmail(String email);

    boolean existsByPhoneNumber(String phoneNumber);

    List<Restaurant> findByOpenTrue();
}

