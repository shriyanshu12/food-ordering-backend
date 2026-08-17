package com.foodapp.food_ordering_backend.repository;

import com.foodapp.food_ordering_backend.entity.MenuCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MenuCategoryRepository
        extends JpaRepository<MenuCategory, Long> {

    boolean existsByRestaurantIdAndNameIgnoreCase(
            Long restaurantId,
            String name
    );

    List<MenuCategory> findByRestaurantId(Long restaurantId);

    boolean existsByIdAndRestaurantId(
            Long categoryId,
            Long restaurantId
    );
}