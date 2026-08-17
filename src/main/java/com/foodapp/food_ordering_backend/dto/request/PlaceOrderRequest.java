package com.foodapp.food_ordering_backend.dto.request;

import com.foodapp.food_ordering_backend.entity.enums.PaymentMethod;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlaceOrderRequest {

    @NotBlank
    private String deliveryAddress;

    @NotBlank
    private String phoneNumber;

    @NotNull
    private PaymentMethod paymentMethod;
}