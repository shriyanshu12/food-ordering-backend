package com.foodapp.food_ordering_backend.mapper;

import com.foodapp.food_ordering_backend.dto.request.MenuItemRequest;
import com.foodapp.food_ordering_backend.dto.response.MenuItemResponse;
import com.foodapp.food_ordering_backend.entity.MenuCategory;
import com.foodapp.food_ordering_backend.entity.MenuItem;
import com.foodapp.food_ordering_backend.entity.Restaurant;
import org.springframework.stereotype.Component;

@Component
public class MenuItemMapper {

    public MenuItem toEntity(
            MenuItemRequest request,
            Restaurant restaurant,
            MenuCategory category) {

        return MenuItem.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .imageUrl(request.getImageUrl())
                .veg(request.getVeg())
                .available(request.getAvailable())
                .preparationTime(request.getPreparationTime())
                .restaurant(restaurant)
                .category(category)
                .build();
    }

    public MenuItemResponse toResponse(MenuItem item) {

        return MenuItemResponse.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .price(item.getPrice())
                .imageUrl(item.getImageUrl())
                .veg(item.getVeg())
                .available(item.getAvailable())
                .preparationTime(item.getPreparationTime())
                .restaurantId(item.getRestaurant().getId())
                .restaurantName(item.getRestaurant().getName())
                .categoryId(item.getCategory().getId())
                .categoryName(item.getCategory().getName())
                .build();
    }

    public void updateEntity(
            MenuItem item,
            MenuItemRequest request,
            Restaurant restaurant,
            MenuCategory category) {

        item.setName(request.getName());
        item.setDescription(request.getDescription());
        item.setPrice(request.getPrice());
        item.setImageUrl(request.getImageUrl());
        item.setVeg(request.getVeg());
        item.setAvailable(request.getAvailable());
        item.setPreparationTime(request.getPreparationTime());
        item.setRestaurant(restaurant);
        item.setCategory(category);
    }
}