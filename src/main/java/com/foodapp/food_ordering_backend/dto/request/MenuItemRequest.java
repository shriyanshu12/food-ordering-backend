package com.foodapp.food_ordering_backend.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MenuItemRequest {

    @NotBlank(message = "Item name is required")
    @Size(max = 100)
    private String name;

    @Size(max = 500)
    private String description;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal price;

    private String imageUrl;

    @NotNull
    private Boolean veg;

    @NotNull
    private Boolean available;

    @NotNull
    @Min(1)
    private Integer preparationTime;

    @NotNull
    private Long restaurantId;

    @NotNull
    private Long categoryId;
}