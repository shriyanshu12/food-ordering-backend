package com.foodapp.food_ordering_backend.repository;

import com.foodapp.food_ordering_backend.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository
        extends JpaRepository<OrderItem, Long> {
}