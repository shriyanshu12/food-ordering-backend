package com.foodapp.food_ordering_backend.dto.response;

import com.foodapp.food_ordering_backend.entity.enums.OrderStatus;
import com.foodapp.food_ordering_backend.entity.enums.PaymentMethod;
import com.foodapp.food_ordering_backend.entity.enums.PaymentStatus;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponse {

    private Long id;

    private String orderNumber;

    private String restaurantName;

    private String deliveryAddress;

    private String phoneNumber;

    private PaymentMethod paymentMethod;

    private PaymentStatus paymentStatus;

    private OrderStatus orderStatus;

    private BigDecimal totalAmount;

    private List<OrderItemResponse> items;
}