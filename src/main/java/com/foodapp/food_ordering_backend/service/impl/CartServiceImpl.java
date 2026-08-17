package com.foodapp.food_ordering_backend.service.impl;

import com.foodapp.food_ordering_backend.dto.request.AddToCartRequest;
import com.foodapp.food_ordering_backend.dto.request.UpdateCartItemRequest;
import com.foodapp.food_ordering_backend.dto.response.CartResponse;
import com.foodapp.food_ordering_backend.entity.Cart;
import com.foodapp.food_ordering_backend.entity.MenuItem;
import com.foodapp.food_ordering_backend.entity.User;
import com.foodapp.food_ordering_backend.exception.ResourceNotFoundException;
import com.foodapp.food_ordering_backend.mapper.CartMapper;
import com.foodapp.food_ordering_backend.repository.CartItemRepository;
import com.foodapp.food_ordering_backend.repository.CartRepository;
import com.foodapp.food_ordering_backend.repository.MenuItemRepository;
import com.foodapp.food_ordering_backend.repository.UserRepository;
import com.foodapp.food_ordering_backend.service.CartService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import com.foodapp.food_ordering_backend.entity.CartItem;
import com.foodapp.food_ordering_backend.exception.BadRequestException;



@Service
@RequiredArgsConstructor
@Transactional
public class CartServiceImpl implements CartService {

    private static final Logger log =
            LoggerFactory.getLogger(CartServiceImpl.class);
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final MenuItemRepository menuItemRepository;
    private final UserRepository userRepository;
    private final CartMapper cartMapper;

    @Override
    public CartResponse addToCart(AddToCartRequest request) {

        // Logged-in user
        User user = getCurrentUser();

        log.info(
                "User {} is adding menu item ID={} with quantity={}",
                user.getEmail(),
                request.getMenuItemId(),
                request.getQuantity()
        );

        // User cart (create if doesn't exist)
        Cart cart = getOrCreateCart(user);

        // Menu item
        MenuItem menuItem = getMenuItem(request.getMenuItemId());

        // Check availability
        if (!Boolean.TRUE.equals(menuItem.getAvailable())) {

            log.warn(
                    "User {} attempted to add unavailable menu item ID={}",
                    user.getEmail(),
                    menuItem.getId()
            );

            throw new BadRequestException("Menu item is currently unavailable");
        }

        // Allow only one restaurant per cart
        if (!cart.getCartItems().isEmpty()) {

            Long existingRestaurantId = cart.getCartItems()
                    .get(0)
                    .getMenuItem()
                    .getRestaurant()
                    .getId();

            if (!existingRestaurantId.equals(menuItem.getRestaurant().getId())) {

                log.warn(
                        "User {} attempted to add item from another restaurant",
                        user.getEmail()
                );

                throw new BadRequestException(
                        "You can add items from only one restaurant at a time."
                );
            }
        }

        // Check if item already exists
        CartItem cartItem = cartItemRepository
                .findByCart_IdAndMenuItem_Id(cart.getId(), menuItem.getId())
                .orElse(null);

        if (cartItem != null) {

            cartItem.setQuantity(
                    cartItem.getQuantity() + request.getQuantity()
            );

            log.info(
                    "Updated quantity of menu item ID={} in cart",
                    menuItem.getId()
            );

        } else {

            cartItem = CartItem.builder()
                    .cart(cart)
                    .menuItem(menuItem)
                    .quantity(request.getQuantity())
                    .unitPrice(menuItem.getPrice())
                    .build();

            cart.getCartItems().add(cartItem);
            log.info(
                    "Added new menu item '{}' to cart",
                    menuItem.getName()
            );
        }

        cartItemRepository.save(cartItem);

        log.info(
                "Cart updated successfully for user {}",
                user.getEmail()
        );

        return cartMapper.toCartResponse(cart);
    }

    @Override
    public CartResponse getCart() {

        User user = getCurrentUser();

        log.info(
                "Fetching cart for user {}",
                user.getEmail()
        );

        Cart cart = getOrCreateCart(user);

        log.info(
                "Cart fetched successfully for user {}",
                user.getEmail()
        );

        return cartMapper.toCartResponse(cart);
    }

    @Override
    public CartResponse updateCartItem(
            Long cartItemId,
            UpdateCartItemRequest request) {

        User user = getCurrentUser();

        log.info(
                "User {} updating cart item ID={}",
                user.getEmail(),
                cartItemId
        );

        Cart cart = getOrCreateCart(user);

        CartItem cartItem = getCartItem(cartItemId);

        // Security check
        if (!cartItem.getCart().getId().equals(cart.getId())) {

            log.warn(
                    "User {} attempted to update cart item not belonging to their cart",
                    user.getEmail()
            );

            throw new BadRequestException("Cart item does not belong to your cart");
        }

        cartItem.setQuantity(request.getQuantity());

        cartItemRepository.save(cartItem);

        log.info(
                "Cart item updated successfully. ID={}, New Quantity={}",
                cartItem.getId(),
                cartItem.getQuantity()
        );

        return cartMapper.toCartResponse(cart);
    }

    @Override
    public void removeCartItem(Long cartItemId) {

        User user = getCurrentUser();

        Cart cart = getOrCreateCart(user);

        log.info(
                "User {} removing cart item ID={}",
                user.getEmail(),
                cartItemId
        );

        CartItem cartItem = getCartItem(cartItemId);

        if (!cartItem.getCart().getId().equals(cart.getId())) {

            log.warn(
                    "User {} attempted to remove another user's cart item",
                    user.getEmail()
            );

            throw new BadRequestException("Cart item does not belong to your cart");
        }

        cartItemRepository.delete(cartItem);

        log.info(
                "Cart item removed successfully. ID={}",
                cartItemId
        );
    }

    @Override
    public void clearCart() {

        User user = getCurrentUser();

        log.info(
                "Clearing cart for user {}",
                user.getEmail()
        );

        Cart cart = getOrCreateCart(user);

        cartItemRepository.deleteByCart_Id(cart.getId());

        cart.getCartItems().clear();

        log.info(
                "Cart cleared successfully for user {}",
                user.getEmail()
        );
    }

    private User getCurrentUser() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> {

                    log.warn(
                            "Authenticated user not found with email={}",
                            email
                    );

                    return new ResourceNotFoundException("User not found");
                });
    }

    private Cart getOrCreateCart(User user) {

        return cartRepository.findByUser_Id(user.getId())
                .orElseGet(() -> {

                    log.info(
                            "Creating new cart for user {}",
                            user.getEmail()
                    );

                    Cart cart = Cart.builder()
                            .user(user)
                            .build();

                    log.info(
                            "Cart created successfully. ID={}",
                            cart.getId()
                    );

                    return cartRepository.save(cart);

                });

    }

    private MenuItem getMenuItem(Long menuItemId) {

        return menuItemRepository.findById(menuItemId)
                .orElseThrow(() -> {

                    log.warn(
                            "Menu item not found. ID={}",
                            menuItemId
                    );

                    return new ResourceNotFoundException("Menu item not found");
                });
    }

    private CartItem getCartItem(Long cartItemId) {

        return cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> {

                    log.warn(
                            "Cart item not found. ID={}",
                            cartItemId
                    );

                    return new ResourceNotFoundException("Cart item not found");
                });
    }
}