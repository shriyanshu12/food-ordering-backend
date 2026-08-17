package com.foodapp.food_ordering_backend.service;

import com.foodapp.food_ordering_backend.dto.request.AddToCartRequest;
import com.foodapp.food_ordering_backend.dto.request.UpdateCartItemRequest;
import com.foodapp.food_ordering_backend.dto.response.CartResponse;
import com.foodapp.food_ordering_backend.entity.User;

public interface CartService {

    CartResponse addToCart(AddToCartRequest request);

    CartResponse getCart();

    CartResponse updateCartItem(
            Long cartItemId,
            UpdateCartItemRequest request);

    void removeCartItem(Long cartItemId);

    void clearCart();
}