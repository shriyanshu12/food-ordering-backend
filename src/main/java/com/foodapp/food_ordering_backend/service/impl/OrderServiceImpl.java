package com.foodapp.food_ordering_backend.service.impl;

import com.foodapp.food_ordering_backend.dto.request.PlaceOrderRequest;
import com.foodapp.food_ordering_backend.dto.response.OrderResponse;
import com.foodapp.food_ordering_backend.entity.enums.OrderStatus;
import com.foodapp.food_ordering_backend.repository.OrderRepository;
import com.foodapp.food_ordering_backend.service.OrderService;
import com.foodapp.food_ordering_backend.util.OrderStatusValidator;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.foodapp.food_ordering_backend.entity.*;
import com.foodapp.food_ordering_backend.exception.BadRequestException;
import com.foodapp.food_ordering_backend.exception.ResourceNotFoundException;
import com.foodapp.food_ordering_backend.mapper.OrderMapper;
import com.foodapp.food_ordering_backend.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {

    private static final Logger log =
            LoggerFactory.getLogger(OrderServiceImpl.class);
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final OrderMapper orderMapper;

    @Override
    public OrderResponse placeOrder(PlaceOrderRequest request) {

        User user = getCurrentUser();

        log.info("User {} is placing an order", user.getEmail());

        Cart cart = getCart(user);

        if (cart.getCartItems().isEmpty()) {

            log.warn(
                    "Order placement failed. Cart is empty for user {}",
                    user.getEmail()
            );

            throw new BadRequestException("Your cart is empty");
        }

        Restaurant restaurant = cart.getCartItems()
                .get(0)
                .getMenuItem()
                .getRestaurant();

        log.info(
                "Order will be placed for restaurant '{}'",
                restaurant.getName()
        );

        BigDecimal totalAmount = BigDecimal.ZERO;

        for (CartItem item : cart.getCartItems()) {

            BigDecimal itemTotal = item.getUnitPrice()
                    .multiply(BigDecimal.valueOf(item.getQuantity()));

            totalAmount = totalAmount.add(itemTotal);
        }

        log.info(
                "Order total calculated: {}",
                totalAmount
        );

        Order order = Order.builder()
                .orderNumber(generateOrderNumber())
                .user(user)
                .restaurant(restaurant)
                .deliveryAddress(request.getDeliveryAddress())
                .phoneNumber(request.getPhoneNumber())
                .paymentMethod(request.getPaymentMethod())
                .totalAmount(totalAmount)
                .build();

        order = orderRepository.save(order);

        log.info(
                "Order created successfully. OrderNumber={}, OrderId={}",
                order.getOrderNumber(),
                order.getId()
        );

        for (CartItem cartItem : cart.getCartItems()) {

            BigDecimal totalPrice = cartItem.getUnitPrice()
                    .multiply(BigDecimal.valueOf(cartItem.getQuantity()));

            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .menuItem(cartItem.getMenuItem())
                    .quantity(cartItem.getQuantity())
                    .unitPrice(cartItem.getUnitPrice())
                    .totalPrice(totalPrice)
                    .build();

            orderItemRepository.save(orderItem);

            log.info(
                    "Added '{}' x{} to order {}",
                    cartItem.getMenuItem().getName(),
                    cartItem.getQuantity(),
                    order.getOrderNumber()
            );

            order.getOrderItems().add(orderItem);
        }

        cartItemRepository.deleteByCart_Id(cart.getId());

        cart.getCartItems().clear();

        log.info(
                "Cart cleared after successful order placement for user {}",
                user.getEmail()
        );

        log.info(
                "Order {} placed successfully",
                order.getOrderNumber()
        );
        return orderMapper.toOrderResponse(order);
    }

    @Override
    public Page<OrderResponse> getMyOrders(
            OrderStatus status,
            int page,
            int size) {

        User user = getCurrentUser();

        log.info(
                "Fetching orders for user {}",
                user.getEmail()
        );

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("createdAt").descending()
        );

        Page<Order> orders;

        if (status == null) {

            orders = orderRepository.findByUser_Id(
                    user.getId(),
                    pageable
            );

        } else {

            orders = orderRepository.findByUser_IdAndOrderStatus(
                    user.getId(),
                    status,
                    pageable
            );
        }

        log.info(
                "Fetched {} orders for user {}",
                orders.getTotalElements(),
                user.getEmail()
        );

        return orders.map(orderMapper::toOrderResponse);
    }

    @Override
    public OrderResponse getOrderById(Long orderId) {

        User user = getCurrentUser();

        log.info(
                "User {} requested order ID={}",
                user.getEmail(),
                orderId
        );

        Order order = getOrder(orderId);

        if (!order.getUser().getId().equals(user.getId())) {

            log.warn(
                    "Unauthorized access to order ID={} by {}",
                    orderId,
                    user.getEmail()
            );

            throw new BadRequestException("You are not authorized to view this order");
        }

        log.info(
                "Order {} retrieved successfully",
                order.getOrderNumber()
        );

        return orderMapper.toOrderResponse(order);
    }

    @Override
    public void cancelOrder(Long orderId) {

        User user = getCurrentUser();

        log.info(
                "User {} requested cancellation for order ID={}",
                user.getEmail(),
                orderId
        );

        Order order = getOrder(orderId);

        if (!order.getUser().getId().equals(user.getId())) {

            log.warn(
                    "Unauthorized cancellation attempt for order ID={} by {}",
                    orderId,
                    user.getEmail()
            );

            throw new BadRequestException("You are not authorized to cancel this order");
        }

        if (order.getOrderStatus() != OrderStatus.PENDING) {

            log.warn(
                    "Cancellation rejected. Order {} is in {} state",
                    order.getOrderNumber(),
                    order.getOrderStatus()
            );

            throw new BadRequestException(
                    "Only pending orders can be cancelled"
            );
        }

        order.setOrderStatus(OrderStatus.CANCELLED);

        orderRepository.save(order);

        log.info(
                "Order {} cancelled successfully",
                order.getOrderNumber()
        );
    }

    private User getCurrentUser() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> {

                    log.warn(
                            "Authenticated user not found: {}",
                            email
                    );

                    return new ResourceNotFoundException("User not found");
                });
    }

    private Cart getCart(User user) {

        return cartRepository.findByUser_Id(user.getId())
                .orElseThrow(() -> {

                    log.warn(
                            "Cart not found for user {}",
                            user.getEmail()
                    );

                    return new BadRequestException("Cart not found");
                });
    }

    private String generateOrderNumber() {

        LocalDate today = LocalDate.now();

        LocalDateTime start = today.atStartOfDay();

        LocalDateTime end = today.plusDays(1).atStartOfDay();

        long count = orderRepository.countByCreatedAtBetween(start, end);

        String date = today.format(DateTimeFormatter.BASIC_ISO_DATE);

        return String.format(
                "ORD-%s-%06d",
                date,
                count + 1
        );
    }

    private Order getOrder(Long orderId) {

        return orderRepository.findById(orderId)
                .orElseThrow(() -> {

                    log.warn(
                            "Order not found. ID={}",
                            orderId
                    );

                    return new ResourceNotFoundException("Order not found");
                });
    }

    @Override
    public Page<OrderResponse> getAllOrders(
            OrderStatus status,
            int page,
            int size) {

        log.info(
                "Admin requested all orders. Status={}, Page={}, Size={}",
                status,
                page,
                size
        );

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("createdAt").descending()
        );

        Page<Order> orders;

        if (status == null) {

            orders = orderRepository.findAll(pageable);

        } else {

            orders = orderRepository.findByOrderStatus(
                    status,
                    pageable
            );
        }

        log.info(
                "Admin fetched {} orders",
                orders.getTotalElements()
        );

        return orders.map(orderMapper::toOrderResponse);
    }

    @Override
    public List<OrderResponse> getOrdersByStatus(OrderStatus status) {

        log.info(
                "Admin requested orders with status {}",
                status
        );

        List<OrderResponse> response = orderRepository.findAll()
                .stream()
                .filter(order -> order.getOrderStatus() == status)
                .map(orderMapper::toOrderResponse)
                .toList();

        log.info(
                "Found {} orders with status {}",
                response.size(),
                status
        );

        return response;
    }

    @Override
    public OrderResponse updateOrderStatus(
            Long orderId,
            OrderStatus status) {

        log.info(
                "Updating order ID={} to status {}",
                orderId,
                status
        );

        Order order = getOrder(orderId);

        // It validates whether the status change is allowed
        OrderStatusValidator.validate(
                order.getOrderStatus(),
                status
        );

        OrderStatus previousStatus = order.getOrderStatus();

        OrderStatusValidator.validate(previousStatus, status);

        //Update the status
        order.setOrderStatus(status);

        order = orderRepository.save(order);

        log.info(
                "Order {} status updated from {} to {}",
                order.getOrderNumber(),
                previousStatus,
                status
        );

        return orderMapper.toOrderResponse(order);
    }
}