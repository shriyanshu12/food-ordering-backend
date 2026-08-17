package com.foodapp.food_ordering_backend.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddToCartRequest {

    @NotNull
    private Long menuItemId;

    @NotNull
    @Min(1)
    private Integer quantity;
}