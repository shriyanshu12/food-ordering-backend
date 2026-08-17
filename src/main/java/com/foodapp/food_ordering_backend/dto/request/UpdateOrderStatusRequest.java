package com.foodapp.food_ordering_backend.dto.request;

import com.foodapp.food_ordering_backend.entity.enums.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateOrderStatusRequest {

    @NotNull
    private OrderStatus orderStatus;

}