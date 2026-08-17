package com.foodapp.food_ordering_backend.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItemResponse {

    private Long id;

    private Long menuItemId;

    private String menuItemName;

    private String imageUrl;

    private Boolean veg;

    private Integer quantity;

    private BigDecimal unitPrice;

    private BigDecimal totalPrice;
}