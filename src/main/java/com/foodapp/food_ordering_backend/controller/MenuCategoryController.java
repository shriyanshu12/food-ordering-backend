package com.foodapp.food_ordering_backend.controller;

import com.foodapp.food_ordering_backend.dto.request.MenuCategoryRequest;
import com.foodapp.food_ordering_backend.dto.response.MenuCategoryResponse;
import com.foodapp.food_ordering_backend.service.MenuCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/menu-categories")
@RequiredArgsConstructor
@Tag(
        name = "Menu Categories",
        description = "APIs for managing restaurant menu categories"
)
public class MenuCategoryController {

    private final MenuCategoryService menuCategoryService;

    @Operation(
            summary = "Create Menu Category",
            description = "Creates a new menu category for a restaurant (Admin only)"
    )
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public MenuCategoryResponse createCategory(
            @Valid @RequestBody MenuCategoryRequest request) {

        return menuCategoryService.createCategory(request);
    }

    @Operation(
            summary = "Update Menu Category",
            description = "Updates an existing menu category (Admin only)"
    )
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public MenuCategoryResponse updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody MenuCategoryRequest request) {

        return menuCategoryService.updateCategory(id, request);
    }

    @Operation(
            summary = "Delete Menu Category",
            description = "Deletes a menu category (Admin only)"
    )
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteCategory(@PathVariable Long id) {

        menuCategoryService.deleteCategory(id);
    }

    @Operation(
            summary = "Get Menu Category By ID",
            description = "Returns a menu category by its ID"
    )
    @GetMapping("/{id}")
    public MenuCategoryResponse getCategoryById(
            @PathVariable Long id) {

        return menuCategoryService.getCategoryById(id);
    }

    @Operation(
            summary = "Get Categories By Restaurant",
            description = "Returns all menu categories belonging to a specific restaurant"
    )
    @GetMapping("/restaurant/{restaurantId}")
    public List<MenuCategoryResponse> getCategoriesByRestaurant(
            @PathVariable Long restaurantId) {

        return menuCategoryService.getCategoriesByRestaurant(restaurantId);
    }
}