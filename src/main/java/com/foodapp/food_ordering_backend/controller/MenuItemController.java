package com.foodapp.food_ordering_backend.controller;

import com.foodapp.food_ordering_backend.dto.request.MenuItemRequest;
import com.foodapp.food_ordering_backend.dto.response.MenuItemResponse;
import com.foodapp.food_ordering_backend.service.MenuItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/menu-items")
@RequiredArgsConstructor
@Tag(
        name = "Menu Items",
        description = "APIs for managing restaurant menu items"
)
public class MenuItemController {

    private final MenuItemService menuItemService;

    @Operation(
            summary = "Create Menu Item",
            description = "Creates a new menu item for a restaurant (Admin only)"
    )
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public MenuItemResponse createMenuItem(
            @Valid @RequestBody MenuItemRequest request) {

        return menuItemService.createMenuItem(request);
    }

    @Operation(
            summary = "Update Menu Item",
            description = "Updates an existing menu item (Admin only)"
    )
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public MenuItemResponse updateMenuItem(
            @PathVariable Long id,
            @Valid @RequestBody MenuItemRequest request) {

        return menuItemService.updateMenuItem(id, request);
    }

    @Operation(
            summary = "Delete Menu Item",
            description = "Deletes a menu item (Admin only)"
    )
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteMenuItem(@PathVariable Long id) {

        menuItemService.deleteMenuItem(id);
    }

    @Operation(
            summary = "Get Menu Item By ID",
            description = "Returns a menu item by its unique ID"
    )
    @GetMapping("/{id}")
    public MenuItemResponse getMenuItemById(
            @PathVariable Long id) {

        return menuItemService.getMenuItemById(id);
    }

    @Operation(
            summary = "Search Menu Items",
            description = "Search menu items using restaurant, category, keyword, vegetarian preference, availability, pagination and sorting"
    )
    @GetMapping
    public Page<MenuItemResponse> getMenuItems(

            @RequestParam(required = false) String keyword,

            @RequestParam(required = false) Long restaurantId,

            @RequestParam(required = false) Long categoryId,

            @RequestParam(required = false) Boolean veg,

            @RequestParam(required = false) Boolean available,

            @RequestParam(required = false) Double minPrice,

            @RequestParam(required = false) Double maxPrice,

            @RequestParam(defaultValue = "0") int page,

            @RequestParam(defaultValue = "10") int size,

            @RequestParam(defaultValue = "name") String sortBy,

            @RequestParam(defaultValue = "asc") String direction) {

        return menuItemService.getMenuItems(
                keyword,
                restaurantId,
                categoryId,
                veg,
                available,
                minPrice,
                maxPrice,
                page,
                size,
                sortBy,
                direction
        );
    }
}