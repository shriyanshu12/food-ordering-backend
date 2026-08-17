package com.foodapp.food_ordering_backend.repository;

import com.foodapp.food_ordering_backend.entity.Order;
import com.foodapp.food_ordering_backend.entity.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUser_IdOrderByCreatedAtDesc(Long userId);

    List<Order> findByRestaurant_IdOrderByCreatedAtDesc(Long restaurantId);

    long countByCreatedAtBetween(
            LocalDateTime start,
            LocalDateTime end
    );

    List<Order> findByRestaurant_IdAndOrderStatus(
            Long restaurantId,
            OrderStatus orderStatus
    );

    Optional<Order> findByOrderNumber(String orderNumber);

    List<Order> findAllByOrderByCreatedAtDesc();

    Page<Order> findByUser_Id(
            Long userId,
            Pageable pageable
    );

    Page<Order> findByUser_IdAndOrderStatus(
            Long userId,
            OrderStatus status,
            Pageable pageable
    );

    Page<Order> findByOrderStatus(
            OrderStatus status,
            Pageable pageable
    );
}