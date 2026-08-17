package com.foodapp.food_ordering_backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.foodapp.food_ordering_backend.config.JwtAuthFilter;
import com.foodapp.food_ordering_backend.config.JwtAuthenticationEntryPoint;
import com.foodapp.food_ordering_backend.dto.request.MenuCategoryRequest;
import com.foodapp.food_ordering_backend.dto.response.MenuCategoryResponse;
import com.foodapp.food_ordering_backend.service.MenuCategoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MenuCategoryController.class)
@AutoConfigureMockMvc(addFilters = false)
class MenuCategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private MenuCategoryService menuCategoryService;

    @MockitoBean
    private JwtAuthFilter jwtAuthFilter;

    @MockitoBean
    private JwtAuthenticationEntryPoint authenticationEntryPoint;

    @MockitoBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    private MenuCategoryRequest request() {
        return MenuCategoryRequest.builder()
                .name("Pizza")
                .description("Pizza Category")
                .restaurantId(1L)
                .build();
    }

    private MenuCategoryResponse response() {
        return MenuCategoryResponse.builder()
                .id(1L)
                .name("Pizza")
                .description("Pizza Category")
                .restaurantId(1L)
                .restaurantName("Pizza Hub")
                .build();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createCategory() throws Exception {

        when(menuCategoryService.createCategory(any()))
                .thenReturn(response());

        mockMvc.perform(post("/api/menu-categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Pizza"));
    }

    @Test
    @WithMockUser
    void getCategoryById() throws Exception {

        when(menuCategoryService.getCategoryById(1L))
                .thenReturn(response());

        mockMvc.perform(get("/api/menu-categories/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Pizza"));
    }

    @Test
    @WithMockUser
    void getCategoriesByRestaurant() throws Exception {

        when(menuCategoryService.getCategoriesByRestaurant(1L))
                .thenReturn(List.of(response()));

        mockMvc.perform(get("/api/menu-categories/restaurant/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Pizza"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateCategory() throws Exception {

        when(menuCategoryService.updateCategory(eq(1L), any()))
                .thenReturn(response());

        mockMvc.perform(put("/api/menu-categories/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Pizza"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteCategory() throws Exception {

        doNothing().when(menuCategoryService).deleteCategory(1L);

        mockMvc.perform(delete("/api/menu-categories/1"))
                .andExpect(status().isOk());

        verify(menuCategoryService).deleteCategory(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void invalidRequestReturns400() throws Exception {

        MenuCategoryRequest request = MenuCategoryRequest.builder()
                .name("")
                .restaurantId(null)
                .build();

        mockMvc.perform(post("/api/menu-categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}