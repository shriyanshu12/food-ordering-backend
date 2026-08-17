package com.foodapp.food_ordering_backend.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MenuCategoryResponse {

    private Long id;

    private String name;

    private String description;

    private Long restaurantId;

    private String restaurantName;
}