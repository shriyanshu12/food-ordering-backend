package com.foodapp.food_ordering_backend.service;

import com.foodapp.food_ordering_backend.dto.request.PlaceOrderRequest;
import com.foodapp.food_ordering_backend.dto.response.OrderResponse;
import com.foodapp.food_ordering_backend.entity.enums.OrderStatus;
import org.springframework.data.domain.Page;

import java.util.List;

public interface OrderService {

    OrderResponse placeOrder(PlaceOrderRequest request);

    Page<OrderResponse> getMyOrders(
            OrderStatus status,
            int page,
            int size
    );

    OrderResponse getOrderById(Long orderId);

    void cancelOrder(Long orderId);

    Page<OrderResponse> getAllOrders(
            OrderStatus status,
            int page,
            int size
    );
    List<OrderResponse> getOrdersByStatus(OrderStatus status);

    OrderResponse updateOrderStatus(
            Long orderId,
            OrderStatus status
    );
}