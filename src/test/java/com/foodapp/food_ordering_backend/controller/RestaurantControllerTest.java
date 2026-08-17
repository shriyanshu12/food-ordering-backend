package com.foodapp.food_ordering_backend.controller;

// NOTE:
// This is a starter controller test generated from the controller shared.
// If your security configuration requires additional mocked beans
// (e.g. JwtAuthenticationEntryPoint or JwtUtil), add @MockBean fields as needed.

import com.fasterxml.jackson.databind.ObjectMapper;
import com.foodapp.food_ordering_backend.config.JwtAuthFilter;
import com.foodapp.food_ordering_backend.config.JwtAuthenticationEntryPoint;
import com.foodapp.food_ordering_backend.config.SecurityConfig;
import com.foodapp.food_ordering_backend.dto.request.RestaurantRequest;
import com.foodapp.food_ordering_backend.dto.response.RestaurantResponse;
import com.foodapp.food_ordering_backend.service.RestaurantService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RestaurantController.class)
@Import(SecurityConfig.class)
@AutoConfigureMockMvc(addFilters = false)
class RestaurantControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @MockitoBean
    RestaurantService restaurantService;
    @MockitoBean
    JwtAuthFilter jwtAuthFilter;
    @MockitoBean
    JwtAuthenticationEntryPoint authenticationEntryPoint;

    @MockitoBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    private RestaurantRequest request() {
        return RestaurantRequest.builder()
                .name("Pizza Hub")
                .description("Best Pizza")
                .phoneNumber("9876543210")
                .email("pizza@example.com")
                .address("Main Road")
                .city("Gorakhpur")
                .state("Uttar Pradesh")
                .zipCode("273001")
                .imageUrl("img")
                .build();
    }

    private RestaurantResponse response() {
        return RestaurantResponse.builder()
                .id(1L).name("Pizza Hub").description("Best Pizza")
                .phoneNumber("9876543210").email("pizza@example.com")
                .address("Main Road").city("Gorakhpur")
                .state("Uttar Pradesh").zipCode("273001")
                .imageUrl("img").rating(4.5).open(true).build();
    }

    @Test @WithMockUser(roles="ADMIN")
    void createRestaurant() throws Exception {
        when(restaurantService.createRestaurant(any())).thenReturn(response());
        mockMvc.perform(post("/api/restaurants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Pizza Hub"));
    }

    @Test @WithMockUser(roles="USER")
    void createRestaurantForbidden() throws Exception {
        mockMvc.perform(post("/api/restaurants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request())))
                .andExpect(status().isForbidden());
        verify(restaurantService,never()).createRestaurant(any());
    }

    @Test @WithMockUser
    void getRestaurant() throws Exception {
        when(restaurantService.getRestaurantById(1L)).thenReturn(response());
        mockMvc.perform(get("/api/restaurants/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Pizza Hub"));
    }

    @Test @WithMockUser(roles="ADMIN")
    void updateRestaurant() throws Exception {
        when(restaurantService.updateRestaurant(eq(1L),any())).thenReturn(response());
        mockMvc.perform(put("/api/restaurants/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request())))
                .andExpect(status().isOk());
    }

    @Test @WithMockUser(roles="ADMIN")
    void deleteRestaurant() throws Exception {
        doNothing().when(restaurantService).deleteRestaurant(1L);
        mockMvc.perform(delete("/api/restaurants/1"))
                .andExpect(status().isNoContent());
    }

    @Test @WithMockUser
    void searchRestaurants() throws Exception {
        Page<RestaurantResponse> page=new PageImpl<>(Collections.singletonList(response()));
        when(restaurantService.getRestaurants(any(),any(),any(),any(),any(),anyInt(),anyInt(),anyString(),anyString()))
                .thenReturn(page);

        mockMvc.perform(get("/api/restaurants")
                        .param("keyword","pizza")
                        .param("city","Gorakhpur")
                        .param("state","Uttar Pradesh")
                        .param("open","true")
                        .param("minRating","4.0"))
                .andExpect(status().isOk());
    }

    @Test @WithMockUser(roles="ADMIN")
    void invalidRequestReturns400() throws Exception {
        RestaurantRequest r=RestaurantRequest.builder().name("").phoneNumber("123").email("bad").build();
        mockMvc.perform(post("/api/restaurants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(r)))
                .andExpect(status().isBadRequest());
    }
}
