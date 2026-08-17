
package com.foodapp.food_ordering_backend.service.impl;

import com.foodapp.food_ordering_backend.dto.request.AddToCartRequest;
import com.foodapp.food_ordering_backend.dto.request.UpdateCartItemRequest;
import com.foodapp.food_ordering_backend.dto.response.CartResponse;
import com.foodapp.food_ordering_backend.entity.Cart;
import com.foodapp.food_ordering_backend.entity.CartItem;
import com.foodapp.food_ordering_backend.entity.MenuItem;
import com.foodapp.food_ordering_backend.entity.Restaurant;
import com.foodapp.food_ordering_backend.entity.User;
import com.foodapp.food_ordering_backend.exception.BadRequestException;
import com.foodapp.food_ordering_backend.exception.ResourceNotFoundException;
import com.foodapp.food_ordering_backend.mapper.CartMapper;
import com.foodapp.food_ordering_backend.repository.CartItemRepository;
import com.foodapp.food_ordering_backend.repository.CartRepository;
import com.foodapp.food_ordering_backend.repository.MenuItemRepository;
import com.foodapp.food_ordering_backend.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceImplTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private MenuItemRepository menuItemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CartMapper cartMapper;

    @InjectMocks
    private CartServiceImpl cartService;


    private User user;
    private Cart cart;
    private Restaurant restaurant;
    private MenuItem menuItem;
    private CartItem cartItem;
    private CartResponse cartResponse;


    @BeforeEach
    void setUp() {

        user = User.builder()
                .email("user@test.com")
                .firstName("Test")
                .lastName("User")
                .build();

        user.setId(1L);


        restaurant = Restaurant.builder()
                .name("Food Palace")
                .build();

        restaurant.setId(1L);


        menuItem = MenuItem.builder()
                .name("Paneer Tikka")
                .price(BigDecimal.valueOf(250))
                .available(true)
                .restaurant(restaurant)
                .build();

        menuItem.setId(100L);


        cart = Cart.builder()
                .user(user)
                .cartItems(new ArrayList<>())
                .build();

        cart.setId(10L);


        cartItem = CartItem.builder()
                .cart(cart)
                .menuItem(menuItem)
                .quantity(2)
                .unitPrice(BigDecimal.valueOf(250))
                .build();

        cartItem.setId(50L);


        cartResponse = new CartResponse();


        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "user@test.com",
                        null
                )
        );
    }


    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }


    // =====================================================
    // ADD TO CART
    // =====================================================

    @Test
    void addToCart_shouldAddNewItemSuccessfully() {

        AddToCartRequest request = new AddToCartRequest();
        request.setMenuItemId(100L);
        request.setQuantity(2);

        when(userRepository.findByEmail("user@test.com"))
                .thenReturn(Optional.of(user));

        when(cartRepository.findByUser_Id(1L))
                .thenReturn(Optional.of(cart));

        when(menuItemRepository.findById(100L))
                .thenReturn(Optional.of(menuItem));

        when(cartItemRepository.findByCart_IdAndMenuItem_Id(10L, 100L))
                .thenReturn(Optional.empty());

        when(cartMapper.toCartResponse(cart))
                .thenReturn(cartResponse);

        CartResponse result = cartService.addToCart(request);

        assertNotNull(result);

        assertEquals(1, cart.getCartItems().size());

        CartItem addedItem = cart.getCartItems().get(0);

        assertEquals(menuItem, addedItem.getMenuItem());
        assertEquals(2, addedItem.getQuantity());
        assertEquals(
                BigDecimal.valueOf(250),
                addedItem.getUnitPrice()
        );

        verify(cartItemRepository)
                .save(any(CartItem.class));

        verify(cartMapper)
                .toCartResponse(cart);
    }


    @Test
    void addToCart_shouldIncreaseQuantity_whenItemAlreadyExists() {

        AddToCartRequest request = new AddToCartRequest();
        request.setMenuItemId(100L);
        request.setQuantity(3);

        cart.getCartItems().add(cartItem);

        when(userRepository.findByEmail("user@test.com"))
                .thenReturn(Optional.of(user));

        when(cartRepository.findByUser_Id(1L))
                .thenReturn(Optional.of(cart));

        when(menuItemRepository.findById(100L))
                .thenReturn(Optional.of(menuItem));

        when(cartItemRepository.findByCart_IdAndMenuItem_Id(10L, 100L))
                .thenReturn(Optional.of(cartItem));

        when(cartMapper.toCartResponse(cart))
                .thenReturn(cartResponse);

        CartResponse result = cartService.addToCart(request);

        assertNotNull(result);

        assertEquals(5, cartItem.getQuantity());

        verify(cartItemRepository)
                .save(cartItem);
    }


    @Test
    void addToCart_shouldThrowException_whenMenuItemNotFound() {

        AddToCartRequest request = new AddToCartRequest();
        request.setMenuItemId(999L);
        request.setQuantity(1);

        when(userRepository.findByEmail("user@test.com"))
                .thenReturn(Optional.of(user));

        when(cartRepository.findByUser_Id(1L))
                .thenReturn(Optional.of(cart));

        when(menuItemRepository.findById(999L))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> cartService.addToCart(request)
        );

        assertEquals(
                "Menu item not found",
                exception.getMessage()
        );

        verify(cartItemRepository, never())
                .save(any(CartItem.class));
    }


    @Test
    void addToCart_shouldThrowException_whenMenuItemUnavailable() {

        AddToCartRequest request = new AddToCartRequest();
        request.setMenuItemId(100L);
        request.setQuantity(1);

        menuItem.setAvailable(false);

        when(userRepository.findByEmail("user@test.com"))
                .thenReturn(Optional.of(user));

        when(cartRepository.findByUser_Id(1L))
                .thenReturn(Optional.of(cart));

        when(menuItemRepository.findById(100L))
                .thenReturn(Optional.of(menuItem));

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> cartService.addToCart(request)
        );

        assertEquals(
                "Menu item is currently unavailable",
                exception.getMessage()
        );

        verify(cartItemRepository, never())
                .save(any(CartItem.class));
    }



    @Test
    void addToCart_shouldThrowException_whenDifferentRestaurant() {

        AddToCartRequest request = new AddToCartRequest();
        request.setMenuItemId(100L);
        request.setQuantity(1);

        Restaurant existingRestaurant = Restaurant.builder()
                .name("Another Restaurant")
                .build();

        existingRestaurant.setId(2L);

        MenuItem existingItem = MenuItem.builder()
                .name("Burger")
                .restaurant(existingRestaurant)
                .build();

        existingItem.setId(200L);

        CartItem existingCartItem = CartItem.builder()
                .cart(cart)
                .menuItem(existingItem)
                .quantity(1)
                .build();

        existingCartItem.setId(60L);

        cart.getCartItems().add(existingCartItem);

        when(userRepository.findByEmail("user@test.com"))
                .thenReturn(Optional.of(user));

        when(cartRepository.findByUser_Id(1L))
                .thenReturn(Optional.of(cart));

        when(menuItemRepository.findById(100L))
                .thenReturn(Optional.of(menuItem));

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> cartService.addToCart(request)
        );

        assertEquals(
                "You can add items from only one restaurant at a time.",
                exception.getMessage()
        );

        verify(cartItemRepository, never())
                .save(any(CartItem.class));
    }




    // =====================================================
    // GET CART
    // =====================================================

    @Test
    void getCart_shouldReturnCurrentUserCart() {

        when(userRepository.findByEmail("user@test.com"))
                .thenReturn(Optional.of(user));

        when(cartRepository.findByUser_Id(1L))
                .thenReturn(Optional.of(cart));

        when(cartMapper.toCartResponse(cart))
                .thenReturn(cartResponse);

        CartResponse result = cartService.getCart();

        assertNotNull(result);

        verify(userRepository)
                .findByEmail("user@test.com");

        verify(cartRepository)
                .findByUser_Id(1L);

        verify(cartMapper)
                .toCartResponse(cart);
    }


    @Test
    void getCart_shouldCreateCart_whenCartDoesNotExist() {

        when(userRepository.findByEmail("user@test.com"))
                .thenReturn(Optional.of(user));

        when(cartRepository.findByUser_Id(1L))
                .thenReturn(Optional.empty());

        when(cartRepository.save(any(Cart.class)))
                .thenReturn(cart);

        when(cartMapper.toCartResponse(cart))
                .thenReturn(cartResponse);

        CartResponse result = cartService.getCart();

        assertNotNull(result);

        verify(cartRepository)
                .save(any(Cart.class));

        verify(cartMapper)
                .toCartResponse(cart);
    }


    // =====================================================
    // UPDATE CART ITEM
    // =====================================================

    @Test
    void updateCartItem_shouldUpdateQuantitySuccessfully() {

        UpdateCartItemRequest request =
                new UpdateCartItemRequest();

        request.setQuantity(5);

        cart.getCartItems().add(cartItem);

        when(userRepository.findByEmail("user@test.com"))
                .thenReturn(Optional.of(user));

        when(cartRepository.findByUser_Id(1L))
                .thenReturn(Optional.of(cart));

        when(cartItemRepository.findById(50L))
                .thenReturn(Optional.of(cartItem));

        when(cartMapper.toCartResponse(cart))
                .thenReturn(cartResponse);

        CartResponse result =
                cartService.updateCartItem(50L, request);

        assertNotNull(result);

        assertEquals(5, cartItem.getQuantity());

        verify(cartItemRepository)
                .save(cartItem);

        verify(cartMapper)
                .toCartResponse(cart);
    }


    @Test
    void updateCartItem_shouldThrowException_whenItemDoesNotBelongToCart() {

        UpdateCartItemRequest request =
                new UpdateCartItemRequest();

        request.setQuantity(5);

        Cart anotherCart = Cart.builder()
                .user(user)
                .cartItems(new ArrayList<>())
                .build();

        anotherCart.setId(999L);
        cartItem.setCart(anotherCart);

        when(userRepository.findByEmail("user@test.com"))
                .thenReturn(Optional.of(user));

        when(cartRepository.findByUser_Id(1L))
                .thenReturn(Optional.of(cart));

        when(cartItemRepository.findById(50L))
                .thenReturn(Optional.of(cartItem));

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> cartService.updateCartItem(
                        50L,
                        request
                )
        );

        assertEquals(
                "Cart item does not belong to your cart",
                exception.getMessage()
        );

        verify(cartItemRepository, never())
                .save(any(CartItem.class));
    }


    @Test
    void updateCartItem_shouldThrowException_whenCartItemNotFound() {

        UpdateCartItemRequest request =
                new UpdateCartItemRequest();

        request.setQuantity(5);

        when(userRepository.findByEmail("user@test.com"))
                .thenReturn(Optional.of(user));

        when(cartRepository.findByUser_Id(1L))
                .thenReturn(Optional.of(cart));

        when(cartItemRepository.findById(999L))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> cartService.updateCartItem(
                        999L,
                        request
                )
        );

        assertEquals(
                "Cart item not found",
                exception.getMessage()
        );

        verify(cartItemRepository, never())
                .save(any(CartItem.class));
    }


    // =====================================================
    // REMOVE CART ITEM
    // =====================================================

    @Test
    void removeCartItem_shouldRemoveSuccessfully() {

        cart.getCartItems().add(cartItem);

        when(userRepository.findByEmail("user@test.com"))
                .thenReturn(Optional.of(user));

        when(cartRepository.findByUser_Id(1L))
                .thenReturn(Optional.of(cart));

        when(cartItemRepository.findById(50L))
                .thenReturn(Optional.of(cartItem));

        cartService.removeCartItem(50L);

        verify(cartItemRepository)
                .delete(cartItem);
    }


    @Test
    void removeCartItem_shouldThrowException_whenItemBelongsToAnotherCart() {

        Cart anotherCart = Cart.builder()
                .user(user)
                .cartItems(new ArrayList<>())
                .build();

        anotherCart.setId(999L);
        cartItem.setCart(anotherCart);

        when(userRepository.findByEmail("user@test.com"))
                .thenReturn(Optional.of(user));

        when(cartRepository.findByUser_Id(1L))
                .thenReturn(Optional.of(cart));

        when(cartItemRepository.findById(50L))
                .thenReturn(Optional.of(cartItem));

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> cartService.removeCartItem(50L)
        );

        assertEquals(
                "Cart item does not belong to your cart",
                exception.getMessage()
        );

        verify(cartItemRepository, never())
                .delete(any(CartItem.class));
    }


    @Test
    void removeCartItem_shouldThrowException_whenCartItemNotFound() {

        when(userRepository.findByEmail("user@test.com"))
                .thenReturn(Optional.of(user));

        when(cartRepository.findByUser_Id(1L))
                .thenReturn(Optional.of(cart));

        when(cartItemRepository.findById(999L))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> cartService.removeCartItem(999L)
        );

        assertEquals(
                "Cart item not found",
                exception.getMessage()
        );

        verify(cartItemRepository, never())
                .delete(any(CartItem.class));
    }


    // =====================================================
    // CLEAR CART
    // =====================================================

    @Test
    void clearCart_shouldRemoveAllItems() {

        cart.getCartItems().add(cartItem);

        when(userRepository.findByEmail("user@test.com"))
                .thenReturn(Optional.of(user));

        when(cartRepository.findByUser_Id(1L))
                .thenReturn(Optional.of(cart));

        cartService.clearCart();

        verify(cartItemRepository)
                .deleteByCart_Id(10L);

        assertTrue(cart.getCartItems().isEmpty());
    }


    // =====================================================
    // USER NOT FOUND
    // =====================================================

    @Test
    void getCart_shouldThrowException_whenUserNotFound() {

        when(userRepository.findByEmail("user@test.com"))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> cartService.getCart()
        );

        assertEquals(
                "User not found",
                exception.getMessage()
        );

        verify(cartRepository, never())
                .findByUser_Id(anyLong());
    }
}

