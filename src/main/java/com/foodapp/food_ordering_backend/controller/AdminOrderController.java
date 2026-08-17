package com.foodapp.food_ordering_backend.controller;

import com.foodapp.food_ordering_backend.dto.request.UpdateOrderStatusRequest;
import com.foodapp.food_ordering_backend.dto.response.OrderResponse;
import com.foodapp.food_ordering_backend.entity.enums.OrderStatus;
import com.foodapp.food_ordering_backend.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/orders")
@RequiredArgsConstructor
@Tag(
        name = "Admin Order Management",
        description = "APIs for administrators to monitor and manage customer orders"
)
@PreAuthorize("hasRole('ADMIN')")
public class AdminOrderController {

    private final OrderService orderService;

    @Operation(
            summary = "Get All Orders",
            description = "Returns paginated customer orders with optional filtering by order status. Accessible only to administrators."
    )
    @GetMapping
    public ResponseEntity<Page<OrderResponse>> getAllOrders(

            @RequestParam(required = false)
            OrderStatus status,

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "10")
            int size
    ) {

        return ResponseEntity.ok(
                orderService.getAllOrders(
                        status,
                        page,
                        size
                )
        );
    }

    @Operation(
            summary = "Update Order Status",
            description = "Updates the status of a customer order while enforcing valid order status transitions. Accessible only to administrators."
    )
    @PatchMapping("/{orderId}/status")
    public ResponseEntity<OrderResponse> updateStatus(
            @PathVariable Long orderId,
            @Valid @RequestBody UpdateOrderStatusRequest request) {

        return ResponseEntity.ok(
                orderService.updateOrderStatus(
                        orderId,
                        request.getOrderStatus()
                )
        );
    }
}