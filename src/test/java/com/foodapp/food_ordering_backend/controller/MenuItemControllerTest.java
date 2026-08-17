package com.foodapp.food_ordering_backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.foodapp.food_ordering_backend.config.JwtAuthFilter;
import com.foodapp.food_ordering_backend.config.JwtAuthenticationEntryPoint;
import com.foodapp.food_ordering_backend.dto.request.MenuItemRequest;
import com.foodapp.food_ordering_backend.dto.response.MenuItemResponse;
import com.foodapp.food_ordering_backend.service.MenuItemService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MenuItemController.class)
@AutoConfigureMockMvc(addFilters = false)
class MenuItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private MenuItemService menuItemService;

    @MockitoBean
    private JwtAuthFilter jwtAuthFilter;

    @MockitoBean
    private JwtAuthenticationEntryPoint authenticationEntryPoint;

    @MockitoBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    private MenuItemRequest request() {
        return MenuItemRequest.builder()
                .name("Veg Pizza")
                .description("Delicious Pizza")
                .price(BigDecimal.valueOf(299))
                .imageUrl("pizza.jpg")
                .veg(true)
                .available(true)
                .preparationTime(20)
                .restaurantId(1L)
                .categoryId(1L)
                .build();
    }

    private MenuItemResponse response() {
        return MenuItemResponse.builder()
                .id(1L)
                .name("Veg Pizza")
                .description("Delicious Pizza")
                .price(BigDecimal.valueOf(299))
                .imageUrl("pizza.jpg")
                .veg(true)
                .available(true)
                .preparationTime(20)
                .restaurantId(1L)
                .restaurantName("Pizza Hub")
                .categoryId(1L)
                .categoryName("Pizza")
                .build();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createMenuItem() throws Exception {

        when(menuItemService.createMenuItem(any()))
                .thenReturn(response());

        mockMvc.perform(post("/api/menu-items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Veg Pizza"))
                .andExpect(jsonPath("$.price").value(299));
    }

    @Test
    @WithMockUser
    void getMenuItemById() throws Exception {

        when(menuItemService.getMenuItemById(1L))
                .thenReturn(response());

        mockMvc.perform(get("/api/menu-items/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Veg Pizza"));
    }

    @Test
    @WithMockUser
    void searchMenuItems() throws Exception {

        Page<MenuItemResponse> page =
                new PageImpl<>(Collections.singletonList(response()));

        when(menuItemService.getMenuItems(
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                anyInt(),
                anyInt(),
                anyString(),
                anyString()))
                .thenReturn(page);

        mockMvc.perform(get("/api/menu-items")
                        .param("keyword", "pizza")
                        .param("restaurantId", "1")
                        .param("categoryId", "1")
                        .param("veg", "true")
                        .param("available", "true")
                        .param("minPrice", "100")
                        .param("maxPrice", "500"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Veg Pizza"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateMenuItem() throws Exception {

        when(menuItemService.updateMenuItem(eq(1L), any()))
                .thenReturn(response());

        mockMvc.perform(put("/api/menu-items/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Veg Pizza"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteMenuItem() throws Exception {

        doNothing().when(menuItemService).deleteMenuItem(1L);

        mockMvc.perform(delete("/api/menu-items/1"))
                .andExpect(status().isOk());

        verify(menuItemService).deleteMenuItem(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void invalidRequestReturns400() throws Exception {

        MenuItemRequest invalidRequest = MenuItemRequest.builder()
                .name("")
                .price(null)
                .veg(null)
                .available(null)
                .preparationTime(null)
                .restaurantId(null)
                .categoryId(null)
                .build();

        mockMvc.perform(post("/api/menu-items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }
}