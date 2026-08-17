package com.foodapp.food_ordering_backend.util;

import com.foodapp.food_ordering_backend.entity.enums.OrderStatus;
import com.foodapp.food_ordering_backend.exception.BadRequestException;

import java.util.Map;
import java.util.Set;

public final class OrderStatusValidator {

    private OrderStatusValidator() {
    }

    private static final Map<OrderStatus, Set<OrderStatus>> ALLOWED_TRANSITIONS =
            Map.of(
                    OrderStatus.PENDING,
                    Set.of(OrderStatus.CONFIRMED, OrderStatus.CANCELLED),

                    OrderStatus.CONFIRMED,
                    Set.of(OrderStatus.PREPARING),

                    OrderStatus.PREPARING,
                    Set.of(OrderStatus.OUT_FOR_DELIVERY),

                    OrderStatus.OUT_FOR_DELIVERY,
                    Set.of(OrderStatus.DELIVERED),

                    OrderStatus.DELIVERED,
                    Set.of(),

                    OrderStatus.CANCELLED,
                    Set.of()
            );

    public static void validate(
            OrderStatus current,
            OrderStatus next
    ) {

        if (current == next) {
            return;
        }

        Set<OrderStatus> allowed =
                ALLOWED_TRANSITIONS.getOrDefault(current, Set.of());

        if (!allowed.contains(next)) {

            throw new BadRequestException(
                    "Cannot change order status from "
                            + current
                            + " to "
                            + next
            );
        }
    }
}