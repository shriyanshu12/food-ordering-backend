package com.foodapp.food_ordering_backend.mapper;

import com.foodapp.food_ordering_backend.dto.response.CartItemResponse;
import com.foodapp.food_ordering_backend.dto.response.CartResponse;
import com.foodapp.food_ordering_backend.entity.Cart;
import com.foodapp.food_ordering_backend.entity.CartItem;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class CartMapper {

    public CartItemResponse toCartItemResponse(CartItem cartItem) {

        BigDecimal totalPrice =
                cartItem.getUnitPrice()
                        .multiply(BigDecimal.valueOf(cartItem.getQuantity()));

        return CartItemResponse.builder()
                .id(cartItem.getId())
                .menuItemId(cartItem.getMenuItem().getId())
                .menuItemName(cartItem.getMenuItem().getName())
                .imageUrl(cartItem.getMenuItem().getImageUrl())
                .veg(cartItem.getMenuItem().getVeg())
                .quantity(cartItem.getQuantity())
                .unitPrice(cartItem.getUnitPrice())
                .totalPrice(totalPrice)
                .build();
    }

    public CartResponse toCartResponse(Cart cart) {

        List<CartItemResponse> items =
                cart.getCartItems()
                        .stream()
                        .map(this::toCartItemResponse)
                        .toList();

        BigDecimal subtotal =
                items.stream()
                        .map(CartItemResponse::getTotalPrice)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

        int totalItems =
                items.stream()
                        .mapToInt(CartItemResponse::getQuantity)
                        .sum();

        return CartResponse.builder()
                .cartId(cart.getId())
                .userId(cart.getUser().getId())
                .items(items)
                .totalItems(totalItems)
                .subtotal(subtotal)
                .build();
    }
}