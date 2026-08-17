package com.foodapp.food_ordering_backend.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartResponse {

    private Long cartId;

    private Long userId;

    private List<CartItemResponse> items;

    private Integer totalItems;

    private BigDecimal subtotal;
}