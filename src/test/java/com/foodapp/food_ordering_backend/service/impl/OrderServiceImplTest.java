        package com.foodapp.food_ordering_backend.service.impl;

import com.foodapp.food_ordering_backend.dto.request.PlaceOrderRequest;
import com.foodapp.food_ordering_backend.dto.response.OrderResponse;
import com.foodapp.food_ordering_backend.entity.*;
import com.foodapp.food_ordering_backend.entity.enums.OrderStatus;
import com.foodapp.food_ordering_backend.entity.enums.PaymentMethod;
import com.foodapp.food_ordering_backend.exception.BadRequestException;
import com.foodapp.food_ordering_backend.exception.ResourceNotFoundException;
import com.foodapp.food_ordering_backend.mapper.OrderMapper;
import com.foodapp.food_ordering_backend.repository.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.data.domain.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private OrderMapper orderMapper;

    @InjectMocks
    private OrderServiceImpl orderService;

    private User user;
    private Restaurant restaurant;
    private MenuItem menuItem;
    private Cart cart;
    private CartItem cartItem;
    private Order order;
    private OrderResponse orderResponse;
    private PlaceOrderRequest request;

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

        restaurant.setId(2L);

        menuItem = MenuItem.builder()
                .name("Paneer Tikka")
                .price(BigDecimal.valueOf(250))
                .available(true)
                .restaurant(restaurant)
                .build();

        menuItem.setId(10L);

        cart = Cart.builder()
                .user(user)
                .cartItems(new ArrayList<>())
                .build();

        cart.setId(20L);

        cartItem = CartItem.builder()
                .cart(cart)
                .menuItem(menuItem)
                .quantity(2)
                .unitPrice(BigDecimal.valueOf(250))
                .build();

        cartItem.setId(30L);

        cart.getCartItems().add(cartItem);

        order = Order.builder()
                .orderNumber("ORD-20260731-000001")
                .user(user)
                .restaurant(restaurant)
                .deliveryAddress("Kolkata")
                .phoneNumber("9999999999")
                .paymentMethod(PaymentMethod.COD)
                .totalAmount(BigDecimal.valueOf(500))
                .orderItems(new ArrayList<>())
                .build();

        order.setId(100L);

        orderResponse = OrderResponse.builder()
                .id(100L)
                .orderNumber("ORD-20260731-000001")
                .restaurantName("Food Palace")
                .deliveryAddress("Kolkata")
                .phoneNumber("9999999999")
                .paymentMethod(PaymentMethod.COD)
                .totalAmount(BigDecimal.valueOf(500))
                .build();

        request = PlaceOrderRequest.builder()
                .deliveryAddress("Kolkata")
                .phoneNumber("9999999999")
                .paymentMethod(PaymentMethod.COD)
                .build();

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


    // =========================================================
    // PLACE ORDER
    // =========================================================

    @Test
    void placeOrder_shouldCreateOrderSuccessfully() {

        when(userRepository.findByEmail("user@test.com"))
                .thenReturn(Optional.of(user));

        when(cartRepository.findByUser_Id(1L))
                .thenReturn(Optional.of(cart));

        when(orderRepository.countByCreatedAtBetween(any(), any()))
                .thenReturn(0L);

        when(orderRepository.save(any(Order.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(orderItemRepository.save(any(OrderItem.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(orderMapper.toOrderResponse(any(Order.class)))
                .thenReturn(orderResponse);

        OrderResponse response = orderService.placeOrder(request);

        assertNotNull(response);
        assertEquals("ORD-20260731-000001", response.getOrderNumber());
        assertEquals(BigDecimal.valueOf(500), response.getTotalAmount());

        verify(orderRepository).save(any(Order.class));
        verify(orderItemRepository).save(any(OrderItem.class));
        verify(cartItemRepository).deleteByCart_Id(20L);
        verify(orderMapper).toOrderResponse(any(Order.class));
    }


    @Test
    void placeOrder_shouldThrowException_whenCartIsEmpty() {

        cart.getCartItems().clear();

        when(userRepository.findByEmail("user@test.com"))
                .thenReturn(Optional.of(user));

        when(cartRepository.findByUser_Id(1L))
                .thenReturn(Optional.of(cart));

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> orderService.placeOrder(request)
        );

        assertEquals("Your cart is empty", exception.getMessage());

        verify(orderRepository, never()).save(any());
        verify(orderItemRepository, never()).save(any());
    }


    @Test
    void placeOrder_shouldThrowException_whenCartNotFound() {

        when(userRepository.findByEmail("user@test.com"))
                .thenReturn(Optional.of(user));

        when(cartRepository.findByUser_Id(1L))
                .thenReturn(Optional.empty());

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> orderService.placeOrder(request)
        );

        assertEquals("Cart not found", exception.getMessage());

        verify(orderRepository, never()).save(any());
    }


    @Test
    void placeOrder_shouldCalculateTotalAmountCorrectly() {

        CartItem secondItem = CartItem.builder()
                .cart(cart)
                .menuItem(menuItem)
                .quantity(1)
                .unitPrice(BigDecimal.valueOf(100))
                .build();

        secondItem.setId(31L);

        cart.getCartItems().add(secondItem);

        when(userRepository.findByEmail("user@test.com"))
                .thenReturn(Optional.of(user));

        when(cartRepository.findByUser_Id(1L))
                .thenReturn(Optional.of(cart));

        when(orderRepository.countByCreatedAtBetween(any(), any()))
                .thenReturn(0L);

        when(orderRepository.save(any(Order.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(orderItemRepository.save(any(OrderItem.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(orderMapper.toOrderResponse(any(Order.class)))
                .thenReturn(orderResponse);

        orderService.placeOrder(request);

        ArgumentCaptor<Order> orderCaptor =
                ArgumentCaptor.forClass(Order.class);

        verify(orderRepository).save(orderCaptor.capture());

        Order savedOrder = orderCaptor.getValue();

        assertEquals(
                BigDecimal.valueOf(600),
                savedOrder.getTotalAmount()
        );
    }


    @Test
    void placeOrder_shouldClearCartAfterSuccessfulOrder() {

        when(userRepository.findByEmail("user@test.com"))
                .thenReturn(Optional.of(user));

        when(cartRepository.findByUser_Id(1L))
                .thenReturn(Optional.of(cart));

        when(orderRepository.countByCreatedAtBetween(any(), any()))
                .thenReturn(0L);

        when(orderRepository.save(any(Order.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(orderItemRepository.save(any(OrderItem.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(orderMapper.toOrderResponse(any(Order.class)))
                .thenReturn(orderResponse);

        orderService.placeOrder(request);

        verify(cartItemRepository)
                .deleteByCart_Id(20L);

        assertTrue(cart.getCartItems().isEmpty());
    }


    // =========================================================
    // GET MY ORDERS
    // =========================================================

    @Test
    void getMyOrders_shouldReturnOrders() {

        Page<Order> page =
                new PageImpl<>(List.of(order));

        when(userRepository.findByEmail("user@test.com"))
                .thenReturn(Optional.of(user));

        when(orderRepository.findByUser_Id(
                eq(1L),
                any(Pageable.class)
        )).thenReturn(page);

        when(orderMapper.toOrderResponse(order))
                .thenReturn(orderResponse);

        Page<OrderResponse> response =
                orderService.getMyOrders(null, 0, 10);

        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
        assertEquals(
                orderResponse,
                response.getContent().get(0)
        );

        verify(orderRepository)
                .findByUser_Id(eq(1L), any(Pageable.class));
    }


    @Test
    void getMyOrders_shouldFilterByStatus() {

        order.setOrderStatus(OrderStatus.PENDING);

        Page<Order> page =
                new PageImpl<>(List.of(order));

        when(userRepository.findByEmail("user@test.com"))
                .thenReturn(Optional.of(user));

        when(orderRepository.findByUser_IdAndOrderStatus(
                eq(1L),
                eq(OrderStatus.PENDING),
                any(Pageable.class)
        )).thenReturn(page);

        when(orderMapper.toOrderResponse(order))
                .thenReturn(orderResponse);

        Page<OrderResponse> response =
                orderService.getMyOrders(
                        OrderStatus.PENDING,
                        0,
                        10
                );

        assertEquals(1, response.getTotalElements());

        verify(orderRepository)
                .findByUser_IdAndOrderStatus(
                        eq(1L),
                        eq(OrderStatus.PENDING),
                        any(Pageable.class)
                );
    }


    // =========================================================
    // GET ORDER BY ID
    // =========================================================

    @Test
    void getOrderById_shouldReturnOrder_whenAuthorized() {

        when(userRepository.findByEmail("user@test.com"))
                .thenReturn(Optional.of(user));

        when(orderRepository.findById(100L))
                .thenReturn(Optional.of(order));

        when(orderMapper.toOrderResponse(order))
                .thenReturn(orderResponse);

        OrderResponse response =
                orderService.getOrderById(100L);

        assertNotNull(response);
        assertEquals(100L, response.getId());
        assertEquals(
                "ORD-20260731-000001",
                response.getOrderNumber()
        );
    }


    @Test
    void getOrderById_shouldThrowException_whenUnauthorized() {

        User anotherUser = User.builder()
                .email("another@test.com")
                .build();

        anotherUser.setId(99L);

        order.setUser(anotherUser);

        when(userRepository.findByEmail("user@test.com"))
                .thenReturn(Optional.of(user));

        when(orderRepository.findById(100L))
                .thenReturn(Optional.of(order));

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> orderService.getOrderById(100L)
        );

        assertEquals(
                "You are not authorized to view this order",
                exception.getMessage()
        );

        verify(orderMapper, never())
                .toOrderResponse(any());
    }


    @Test
    void getOrderById_shouldThrowException_whenOrderNotFound() {

        when(userRepository.findByEmail("user@test.com"))
                .thenReturn(Optional.of(user));

        when(orderRepository.findById(999L))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> orderService.getOrderById(999L)
        );

        assertEquals(
                "Order not found",
                exception.getMessage()
        );
    }


    // =========================================================
    // CANCEL ORDER
    // =========================================================

    @Test
    void cancelOrder_shouldCancelPendingOrder() {

        order.setOrderStatus(OrderStatus.PENDING);

        when(userRepository.findByEmail("user@test.com"))
                .thenReturn(Optional.of(user));

        when(orderRepository.findById(100L))
                .thenReturn(Optional.of(order));

        when(orderRepository.save(any(Order.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        orderService.cancelOrder(100L);

        assertEquals(
                OrderStatus.CANCELLED,
                order.getOrderStatus()
        );

        verify(orderRepository).save(order);
    }


    @Test
    void cancelOrder_shouldThrowException_whenUnauthorized() {

        User anotherUser = User.builder()
                .email("another@test.com")
                .build();

        anotherUser.setId(99L);

        order.setUser(anotherUser);

        when(userRepository.findByEmail("user@test.com"))
                .thenReturn(Optional.of(user));

        when(orderRepository.findById(100L))
                .thenReturn(Optional.of(order));

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> orderService.cancelOrder(100L)
        );

        assertEquals(
                "You are not authorized to cancel this order",
                exception.getMessage()
        );

        verify(orderRepository, never())
                .save(any(Order.class));
    }


    @Test
    void cancelOrder_shouldThrowException_whenOrderIsNotPending() {

        order.setOrderStatus(OrderStatus.CANCELLED);

        when(userRepository.findByEmail("user@test.com"))
                .thenReturn(Optional.of(user));

        when(orderRepository.findById(100L))
                .thenReturn(Optional.of(order));

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> orderService.cancelOrder(100L)
        );

        assertEquals(
                "Only pending orders can be cancelled",
                exception.getMessage()
        );

        verify(orderRepository, never())
                .save(any(Order.class));
    }


    // =========================================================
    // ADMIN - GET ALL ORDERS
    // =========================================================

    @Test
    void getAllOrders_shouldReturnAllOrders() {

        Page<Order> page =
                new PageImpl<>(List.of(order));

        when(orderRepository.findAll(any(Pageable.class)))
                .thenReturn(page);

        when(orderMapper.toOrderResponse(order))
                .thenReturn(orderResponse);

        Page<OrderResponse> response =
                orderService.getAllOrders(null, 0, 10);

        assertEquals(1, response.getTotalElements());

        verify(orderRepository)
                .findAll(any(Pageable.class));
    }


    @Test
    void getAllOrders_shouldFilterByStatus() {

        Page<Order> page =
                new PageImpl<>(List.of(order));

        when(orderRepository.findByOrderStatus(
                eq(OrderStatus.PENDING),
                any(Pageable.class)
        )).thenReturn(page);

        when(orderMapper.toOrderResponse(order))
                .thenReturn(orderResponse);

        Page<OrderResponse> response =
                orderService.getAllOrders(
                        OrderStatus.PENDING,
                        0,
                        10
                );

        assertEquals(1, response.getTotalElements());

        verify(orderRepository)
                .findByOrderStatus(
                        eq(OrderStatus.PENDING),
                        any(Pageable.class)
                );
    }


    // =========================================================
    // ADMIN - GET ORDERS BY STATUS
    // =========================================================

    @Test
    void getOrdersByStatus_shouldReturnMatchingOrders() {

        Order pendingOrder = Order.builder()
                .orderNumber("ORD-1")
                .orderStatus(OrderStatus.PENDING)
                .build();

        pendingOrder.setId(101L);

        Order cancelledOrder = Order.builder()
                .orderNumber("ORD-2")
                .orderStatus(OrderStatus.CANCELLED)
                .build();

        cancelledOrder.setId(102L);

        when(orderRepository.findAll())
                .thenReturn(List.of(
                        pendingOrder,
                        cancelledOrder
                ));

        when(orderMapper.toOrderResponse(pendingOrder))
                .thenReturn(orderResponse);

        List<OrderResponse> response =
                orderService.getOrdersByStatus(
                        OrderStatus.PENDING
                );

        assertEquals(1, response.size());
        assertEquals(orderResponse, response.get(0));

        verify(orderMapper)
                .toOrderResponse(pendingOrder);

        verify(orderMapper, never())
                .toOrderResponse(cancelledOrder);
    }


    // =========================================================
    // ADMIN - UPDATE ORDER STATUS
    // =========================================================

    @Test
    void updateOrderStatus_shouldUpdateStatusSuccessfully() {

        order.setOrderStatus(OrderStatus.PENDING);

        when(orderRepository.findById(100L))
                .thenReturn(Optional.of(order));

        when(orderRepository.save(any(Order.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(orderMapper.toOrderResponse(any(Order.class)))
                .thenReturn(orderResponse);

        OrderResponse response =
                orderService.updateOrderStatus(
                        100L,
                        OrderStatus.CONFIRMED
                );

        assertNotNull(response);

        assertEquals(
                OrderStatus.CONFIRMED,
                order.getOrderStatus()
        );

        verify(orderRepository).save(order);
        verify(orderMapper).toOrderResponse(order);
    }


    @Test
    void updateOrderStatus_shouldThrowException_whenOrderNotFound() {

        when(orderRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> orderService.updateOrderStatus(
                        999L,
                        OrderStatus.CONFIRMED
                )
        );

        verify(orderRepository, never())
                .save(any(Order.class));
    }
}

