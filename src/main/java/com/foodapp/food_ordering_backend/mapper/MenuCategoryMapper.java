package com.foodapp.food_ordering_backend.mapper;

import com.foodapp.food_ordering_backend.dto.request.MenuCategoryRequest;
import com.foodapp.food_ordering_backend.dto.response.MenuCategoryResponse;
import com.foodapp.food_ordering_backend.entity.MenuCategory;
import com.foodapp.food_ordering_backend.entity.Restaurant;
import org.springframework.stereotype.Component;

@Component
public class MenuCategoryMapper {

    public MenuCategory toEntity(MenuCategoryRequest request, Restaurant restaurant) {

        return MenuCategory.builder()
                .name(request.getName())
                .description(request.getDescription())
                .restaurant(restaurant)
                .build();
    }

    public MenuCategoryResponse toResponse(MenuCategory category) {

        return MenuCategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .restaurantId(category.getRestaurant().getId())
                .restaurantName(category.getRestaurant().getName())
                .build();
    }

    public void updateEntity(
            MenuCategory category,
            MenuCategoryRequest request,
            Restaurant restaurant) {

        category.setName(request.getName());
        category.setDescription(request.getDescription());
        category.setRestaurant(restaurant);
    }
}