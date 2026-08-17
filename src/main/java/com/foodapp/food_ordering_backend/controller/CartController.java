package com.foodapp.food_ordering_backend.controller;

import com.foodapp.food_ordering_backend.dto.request.AddToCartRequest;
import com.foodapp.food_ordering_backend.dto.request.UpdateCartItemRequest;
import com.foodapp.food_ordering_backend.dto.response.CartResponse;
import com.foodapp.food_ordering_backend.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@Tag(
        name = "Shopping Cart",
        description = "APIs for managing the logged-in user's shopping cart"
)
public class CartController {

    private final CartService cartService;

    @Operation(
            summary = "Add Item To Cart",
            description = "Adds a menu item to the logged-in user's cart"
    )
    @PostMapping("/items")
    public ResponseEntity<CartResponse> addToCart(
            @Valid @RequestBody AddToCartRequest request) {

        return ResponseEntity.ok(cartService.addToCart(request));
    }

    @Operation(
            summary = "Get Cart",
            description = "Returns the current logged-in user's shopping cart"
    )
    @GetMapping
    public ResponseEntity<CartResponse> getCart() {

        return ResponseEntity.ok(cartService.getCart());
    }

    @Operation(
            summary = "Update Cart Item",
            description = "Updates the quantity of an existing cart item"
    )
    @PatchMapping("/items/{cartItemId}")
    public ResponseEntity<CartResponse> updateCartItem(
            @PathVariable Long cartItemId,
            @Valid @RequestBody UpdateCartItemRequest request) {

        return ResponseEntity.ok(
                cartService.updateCartItem(cartItemId, request)
        );
    }

    @Operation(
            summary = "Remove Cart Item",
            description = "Removes a specific item from the shopping cart"
    )
    @DeleteMapping("/items/{cartItemId}")
    public ResponseEntity<Void> removeCartItem(
            @PathVariable Long cartItemId) {

        cartService.removeCartItem(cartItemId);

        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Clear Cart",
            description = "Removes all items from the logged-in user's shopping cart"
    )
    @DeleteMapping("/clear")
    public ResponseEntity<Void> clearCart() {

        cartService.clearCart();

        return ResponseEntity.noContent().build();
    }
}