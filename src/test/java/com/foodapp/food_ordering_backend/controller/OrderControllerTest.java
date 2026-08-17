package com.foodapp.food_ordering_backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.foodapp.food_ordering_backend.config.JwtAuthFilter;
import com.foodapp.food_ordering_backend.config.JwtAuthenticationEntryPoint;
import com.foodapp.food_ordering_backend.dto.request.PlaceOrderRequest;
import com.foodapp.food_ordering_backend.dto.response.OrderResponse;
import com.foodapp.food_ordering_backend.entity.enums.OrderStatus;
import com.foodapp.food_ordering_backend.entity.enums.PaymentMethod;
import com.foodapp.food_ordering_backend.entity.enums.PaymentStatus;
import com.foodapp.food_ordering_backend.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
@AutoConfigureMockMvc(addFilters = false)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private OrderService orderService;

    @MockitoBean
    private JwtAuthFilter jwtAuthFilter;

    @MockitoBean
    private JwtAuthenticationEntryPoint authenticationEntryPoint;

    @MockitoBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    private PlaceOrderRequest request() {
        return PlaceOrderRequest.builder()
                .deliveryAddress("Main Road, Gorakhpur")
                .phoneNumber("9876543210")
                .paymentMethod(PaymentMethod.COD)
                .build();
    }

    private OrderResponse response() {
        return OrderResponse.builder()
                .id(1L)
                .orderNumber("ORD1001")
                .restaurantName("Pizza Hub")
                .deliveryAddress("Main Road, Gorakhpur")
                .phoneNumber("9876543210")
                .paymentMethod(PaymentMethod.COD)
                .paymentStatus(PaymentStatus.PENDING)
                .orderStatus(OrderStatus.PENDING)
                .totalAmount(BigDecimal.valueOf(599))
                .items(Collections.emptyList())
                .build();
    }

    @Test
    @WithMockUser
    void placeOrder() throws Exception {

        when(orderService.placeOrder(any()))
                .thenReturn(response());

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.orderNumber").value("ORD1001"));
    }

    @Test
    @WithMockUser
    void getMyOrders() throws Exception {

        Page<OrderResponse> page =
                new PageImpl<>(Collections.singletonList(response()));

        when(orderService.getMyOrders(any(), anyInt(), anyInt()))
                .thenReturn(page);

        mockMvc.perform(get("/api/orders")
                        .param("status", "PENDING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].orderNumber")
                        .value("ORD1001"));
    }

    @Test
    @WithMockUser
    void getOrderById() throws Exception {

        when(orderService.getOrderById(1L))
                .thenReturn(response());

        mockMvc.perform(get("/api/orders/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderNumber")
                        .value("ORD1001"));
    }

    @Test
    @WithMockUser
    void cancelOrder() throws Exception {

        doNothing().when(orderService).cancelOrder(1L);

        mockMvc.perform(patch("/api/orders/1/cancel"))
                .andExpect(status().isNoContent());

        verify(orderService).cancelOrder(1L);
    }

    @Test
    @WithMockUser
    void invalidPlaceOrderReturns400() throws Exception {

        PlaceOrderRequest request = PlaceOrderRequest.builder()
                .deliveryAddress("")
                .phoneNumber("")
                .paymentMethod(null)
                .build();

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}


