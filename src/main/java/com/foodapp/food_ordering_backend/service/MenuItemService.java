package com.foodapp.food_ordering_backend.service;

import com.foodapp.food_ordering_backend.dto.request.MenuItemRequest;
import com.foodapp.food_ordering_backend.dto.response.MenuItemResponse;
import org.springframework.data.domain.Page;

public interface MenuItemService {

    MenuItemResponse createMenuItem(MenuItemRequest request);

    MenuItemResponse updateMenuItem(
            Long id,
            MenuItemRequest request);

    void deleteMenuItem(Long id);

    MenuItemResponse getMenuItemById(Long id);

    Page<MenuItemResponse> getMenuItems(
            String keyword,
            Long restaurantId,
            Long categoryId,
            Boolean veg,
            Boolean available,
            Double minPrice,
            Double maxPrice,
            int page,
            int size,
            String sortBy,
            String direction);
}