package com.foodapp.food_ordering_backend.mapper;

import com.foodapp.food_ordering_backend.dto.response.OrderItemResponse;
import com.foodapp.food_ordering_backend.dto.response.OrderResponse;
import com.foodapp.food_ordering_backend.entity.Order;
import com.foodapp.food_ordering_backend.entity.OrderItem;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderMapper {

    public OrderResponse toOrderResponse(Order order) {

        List<OrderItemResponse> items = order.getOrderItems()
                .stream()
                .map(this::toOrderItemResponse)
                .toList();

        return OrderResponse.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .restaurantName(order.getRestaurant().getName())
                .deliveryAddress(order.getDeliveryAddress())
                .phoneNumber(order.getPhoneNumber())
                .paymentMethod(order.getPaymentMethod())
                .paymentStatus(order.getPaymentStatus())
                .orderStatus(order.getOrderStatus())
                .totalAmount(order.getTotalAmount())
                .items(items)
                .build();
    }

    private OrderItemResponse toOrderItemResponse(OrderItem item) {

        return OrderItemResponse.builder()
                .id(item.getId())
                .menuItemName(item.getMenuItem().getName())
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .totalPrice(item.getTotalPrice())
                .build();
    }
}