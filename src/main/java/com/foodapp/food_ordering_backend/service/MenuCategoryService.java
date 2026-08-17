package com.foodapp.food_ordering_backend.service;

import com.foodapp.food_ordering_backend.dto.request.MenuCategoryRequest;
import com.foodapp.food_ordering_backend.dto.response.MenuCategoryResponse;

import java.util.List;

public interface MenuCategoryService {

    MenuCategoryResponse createCategory(MenuCategoryRequest request);

    MenuCategoryResponse updateCategory(
            Long id,
            MenuCategoryRequest request
    );

    void deleteCategory(Long id);

    MenuCategoryResponse getCategoryById(Long id);

    List<MenuCategoryResponse> getCategoriesByRestaurant(Long restaurantId);
}