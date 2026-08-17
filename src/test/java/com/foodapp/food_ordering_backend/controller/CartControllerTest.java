package com.foodapp.food_ordering_backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.foodapp.food_ordering_backend.config.JwtAuthFilter;
import com.foodapp.food_ordering_backend.config.JwtAuthenticationEntryPoint;
import com.foodapp.food_ordering_backend.dto.request.AddToCartRequest;
import com.foodapp.food_ordering_backend.dto.request.UpdateCartItemRequest;
import com.foodapp.food_ordering_backend.dto.response.CartResponse;
import com.foodapp.food_ordering_backend.service.CartService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
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

@WebMvcTest(CartController.class)
@AutoConfigureMockMvc(addFilters = false)
class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CartService cartService;

    @MockitoBean
    private JwtAuthFilter jwtAuthFilter;

    @MockitoBean
    private JwtAuthenticationEntryPoint authenticationEntryPoint;

    @MockitoBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    private AddToCartRequest addRequest() {
        return AddToCartRequest.builder()
                .menuItemId(1L)
                .quantity(2)
                .build();
    }

    private UpdateCartItemRequest updateRequest() {
        return UpdateCartItemRequest.builder()
                .quantity(3)
                .build();
    }

    private CartResponse response() {
        return CartResponse.builder()
                .cartId(1L)
                .userId(1L)
                .items(Collections.emptyList())
                .totalItems(2)
                .subtotal(BigDecimal.valueOf(599))
                .build();
    }

    @Test
    @WithMockUser
    void addToCart() throws Exception {

        when(cartService.addToCart(any()))
                .thenReturn(response());

        mockMvc.perform(post("/api/cart/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addRequest())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cartId").value(1))
                .andExpect(jsonPath("$.totalItems").value(2));
    }

    @Test
    @WithMockUser
    void getCart() throws Exception {

        when(cartService.getCart())
                .thenReturn(response());

        mockMvc.perform(get("/api/cart"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cartId").value(1))
                .andExpect(jsonPath("$.subtotal").value(599));
    }

    @Test
    @WithMockUser
    void updateCartItem() throws Exception {

        when(cartService.updateCartItem(eq(1L), any()))
                .thenReturn(response());

        mockMvc.perform(patch("/api/cart/items/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cartId").value(1));
    }

    @Test
    @WithMockUser
    void removeCartItem() throws Exception {

        doNothing().when(cartService).removeCartItem(1L);

        mockMvc.perform(delete("/api/cart/items/1"))
                .andExpect(status().isNoContent());

        verify(cartService).removeCartItem(1L);
    }

    @Test
    @WithMockUser
    void clearCart() throws Exception {

        doNothing().when(cartService).clearCart();

        mockMvc.perform(delete("/api/cart/clear"))
                .andExpect(status().isNoContent());

        verify(cartService).clearCart();
    }

    @Test
    @WithMockUser
    void invalidAddToCartRequestReturns400() throws Exception {

        AddToCartRequest request = AddToCartRequest.builder()
                .menuItemId(null)
                .quantity(0)
                .build();

        mockMvc.perform(post("/api/cart/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void invalidUpdateCartItemRequestReturns400() throws Exception {

        UpdateCartItemRequest request = UpdateCartItemRequest.builder()
                .quantity(0)
                .build();

        mockMvc.perform(patch("/api/cart/items/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}