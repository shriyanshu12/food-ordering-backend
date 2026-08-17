package com.foodapp.food_ordering_backend.service.impl;

import com.foodapp.food_ordering_backend.dto.request.MenuItemRequest;
import com.foodapp.food_ordering_backend.dto.response.MenuItemResponse;
import com.foodapp.food_ordering_backend.entity.MenuCategory;
import com.foodapp.food_ordering_backend.entity.MenuItem;
import com.foodapp.food_ordering_backend.entity.Restaurant;
import com.foodapp.food_ordering_backend.exception.BadRequestException;
import com.foodapp.food_ordering_backend.exception.ResourceNotFoundException;
import com.foodapp.food_ordering_backend.mapper.MenuItemMapper;
import com.foodapp.food_ordering_backend.repository.MenuCategoryRepository;
import com.foodapp.food_ordering_backend.repository.MenuItemRepository;
import com.foodapp.food_ordering_backend.repository.RestaurantRepository;
import com.foodapp.food_ordering_backend.service.MenuItemService;
import com.foodapp.food_ordering_backend.specification.MenuItemSpecification;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MenuItemServiceImpl implements MenuItemService {

    private static final Logger log =
            LoggerFactory.getLogger(MenuItemServiceImpl.class);
    private final MenuItemRepository menuItemRepository;
    private final RestaurantRepository restaurantRepository;
    private final MenuCategoryRepository menuCategoryRepository;
    private final MenuItemMapper menuItemMapper;

    @Override
    public MenuItemResponse createMenuItem(MenuItemRequest request) {

        log.info(
                "Creating menu item '{}' for restaurant ID={}",
                request.getName(),
                request.getRestaurantId()
        );
        Restaurant restaurant = restaurantRepository.findById(request.getRestaurantId())
                .orElseThrow(() -> {

                    log.warn(
                            "Menu item creation failed. Restaurant not found. ID={}",
                            request.getRestaurantId()
                    );

                    return new ResourceNotFoundException("Restaurant not found");
                });

        MenuCategory category = menuCategoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> {

                    log.warn(
                            "Menu item creation failed. Category not found. ID={}",
                            request.getCategoryId()
                    );

                    return new ResourceNotFoundException("Category not found");
                });

        if (!menuCategoryRepository.existsByIdAndRestaurantId(
                request.getCategoryId(),
                request.getRestaurantId())) {

            log.warn(
                    "Category ID={} does not belong to restaurant ID={}",
                    request.getCategoryId(),
                    request.getRestaurantId()
            );

            throw new BadRequestException(
                    "Selected category does not belong to the selected restaurant");
        }

        if (menuItemRepository.existsByRestaurantIdAndCategoryIdAndNameIgnoreCase(
                request.getRestaurantId(),
                request.getCategoryId(),
                request.getName())) {

            log.warn(
                    "Menu item '{}' already exists for restaurant ID={} and category ID={}",
                    request.getName(),
                    request.getRestaurantId(),
                    request.getCategoryId()
            );
            throw new BadRequestException(
                    "Menu item already exists in this category");
        }

        MenuItem item =
                menuItemMapper.toEntity(request, restaurant, category);

        item = menuItemRepository.save(item);
        log.info(
                "Menu item created successfully. ID={}, Name={}, Restaurant={}",
                item.getId(),
                item.getName(),
                restaurant.getName()
        );

        return menuItemMapper.toResponse(item);
    }

    @Override
    public MenuItemResponse updateMenuItem(
            Long id,
            MenuItemRequest request) {

        log.info("Updating menu item ID={}", id);

        MenuItem item = menuItemRepository.findById(id)
                .orElseThrow(() -> {

                    log.warn("Menu item not found. ID={}", id);

                    return new ResourceNotFoundException("Menu item not found");
                });

        Restaurant restaurant = restaurantRepository.findById(request.getRestaurantId())
                .orElseThrow(() ->{

                    log.warn(
                            "Restaurant not found while updating menu item. ID={}",
                            request.getRestaurantId()
                    );
                        return new ResourceNotFoundException("Restaurant not found");
                });

        MenuCategory category = menuCategoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> {

                    log.warn(
                            "Category not found while updating menu item. ID={}",
                            request.getCategoryId()
                    );
                    return new ResourceNotFoundException("Category not found");
                });

        if (!menuCategoryRepository.existsByIdAndRestaurantId(
                request.getCategoryId(),
                request.getRestaurantId())) {

            log.warn(
                    "Category ID={} does not belong to restaurant ID={}",
                    request.getCategoryId(),
                    request.getRestaurantId()
            );

            throw new BadRequestException(
                    "Selected category does not belong to the selected restaurant");
        }

        if ((!item.getName().equalsIgnoreCase(request.getName())
                || !item.getCategory().getId().equals(request.getCategoryId()))
                && menuItemRepository.existsByRestaurantIdAndCategoryIdAndNameIgnoreCase(
                request.getRestaurantId(),
                request.getCategoryId(),
                request.getName())) {

            log.warn(
                    "Menu item update failed. '{}' already exists in category ID={}",
                    request.getName(),
                    request.getCategoryId()
            );

            throw new BadRequestException(
                    "Menu item already exists in this category");
        }

        menuItemMapper.updateEntity(item, request, restaurant, category);

        item = menuItemRepository.save(item);

        log.info(
                "Menu item updated successfully. ID={}, Name={}",
                item.getId(),
                item.getName()
        );

        return menuItemMapper.toResponse(item);
    }

    @Override
    public void deleteMenuItem(Long id) {

        log.info("Deleting menu item ID={}", id);

        MenuItem item = menuItemRepository.findById(id)
                .orElseThrow(() -> {

                    log.warn("Menu item not found. ID={}", id);

                    return new ResourceNotFoundException("Menu item not found");
                });

        log.info(
                "Menu item deleted successfully. ID={}, Name={}",
                item.getId(),
                item.getName()
        );

        menuItemRepository.delete(item);
    }

    @Override
    public MenuItemResponse getMenuItemById(Long id) {

        log.info("Fetching menu item ID={}", id);

        MenuItem item = menuItemRepository.findById(id)
                .orElseThrow(() -> {

                    log.warn("Menu item not found. ID={}", id);

                    return new ResourceNotFoundException("Menu item not found");
                });

        return menuItemMapper.toResponse(item);
    }

    @Override
    public Page<MenuItemResponse> getMenuItems(
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
            String direction) {

        log.info(
                "Searching menu items. Keyword={}, RestaurantId={}, CategoryId={}, Veg={}, Available={}, MinPrice={}, MaxPrice={}, Page={}, Size={}",
                keyword,
                restaurantId,
                categoryId,
                veg,
                available,
                minPrice,
                maxPrice,
                page,
                size
        );


        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<MenuItemResponse> response =
                menuItemRepository.findAll(
                                MenuItemSpecification.filterMenuItems(
                                        keyword,
                                        restaurantId,
                                        categoryId,
                                        veg,
                                        available,
                                        minPrice,
                                        maxPrice),
                                pageable)
                        .map(menuItemMapper::toResponse);

        log.info(
                "Menu item search completed. {} records found.",
                response.getTotalElements()
        );

        return response;
    }
}