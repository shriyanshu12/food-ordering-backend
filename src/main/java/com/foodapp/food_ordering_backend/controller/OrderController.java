package com.foodapp.food_ordering_backend.controller;

import com.foodapp.food_ordering_backend.dto.request.PlaceOrderRequest;
import com.foodapp.food_ordering_backend.dto.response.OrderResponse;
import com.foodapp.food_ordering_backend.entity.enums.OrderStatus;
import com.foodapp.food_ordering_backend.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(
        name = "Orders",
        description = "APIs for placing, viewing and managing customer orders"
)
public class OrderController {

    private final OrderService orderService;

    @Operation(
            summary = "Place Order",
            description = "Creates a new order from the authenticated user's shopping cart."
    )
    @PostMapping
    public ResponseEntity<OrderResponse> placeOrder(
            @Valid @RequestBody PlaceOrderRequest request) {

        return ResponseEntity.ok(orderService.placeOrder(request));
    }

    @Operation(
            summary = "Get My Orders",
            description = "Returns paginated orders of the authenticated user. Supports optional filtering by order status."
    )
    @GetMapping
    public ResponseEntity<Page<OrderResponse>> getMyOrders(

            @RequestParam(required = false)
            OrderStatus status,

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "10")
            int size
    ) {

        return ResponseEntity.ok(
                orderService.getMyOrders(
                        status,
                        page,
                        size
                )
        );
    }

    @Operation(
            summary = "Get Order By ID",
            description = "Returns complete details of a specific order belonging to the authenticated user."
    )
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrderById(
            @PathVariable Long orderId) {

        return ResponseEntity.ok(
                orderService.getOrderById(orderId)
        );
    }

    @Operation(
            summary = "Cancel Order",
            description = "Cancels an order if it is still in the PENDING state."
    )
    @PatchMapping("/{orderId}/cancel")
    public ResponseEntity<Void> cancelOrder(
            @PathVariable Long orderId) {

        orderService.cancelOrder(orderId);

        return ResponseEntity.noContent().build();
    }
}