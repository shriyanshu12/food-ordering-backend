package com.foodapp.food_ordering_backend.repository;

import com.foodapp.food_ordering_backend.entity.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface MenuItemRepository
        extends JpaRepository<MenuItem, Long>,
        JpaSpecificationExecutor<MenuItem> {

    boolean existsByRestaurantIdAndCategoryIdAndNameIgnoreCase(
            Long restaurantId,
            Long categoryId,
            String name
    );
}