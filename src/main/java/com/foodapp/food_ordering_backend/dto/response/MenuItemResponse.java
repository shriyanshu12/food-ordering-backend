package com.foodapp.food_ordering_backend.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MenuItemResponse {

    private Long id;

    private String name;

    private String description;

    private BigDecimal price;

    private String imageUrl;

    private Boolean veg;

    private Boolean available;

    private Integer preparationTime;

    private Long restaurantId;

    private String restaurantName;

    private Long categoryId;

    private String categoryName;
}