package com.foodapp.food_ordering_backend.service.impl;

import com.foodapp.food_ordering_backend.dto.request.MenuCategoryRequest;
import com.foodapp.food_ordering_backend.dto.response.MenuCategoryResponse;
import com.foodapp.food_ordering_backend.entity.MenuCategory;
import com.foodapp.food_ordering_backend.entity.Restaurant;
import com.foodapp.food_ordering_backend.exception.BadRequestException;
import com.foodapp.food_ordering_backend.exception.ResourceNotFoundException;
import com.foodapp.food_ordering_backend.mapper.MenuCategoryMapper;
import com.foodapp.food_ordering_backend.repository.MenuCategoryRepository;
import com.foodapp.food_ordering_backend.repository.RestaurantRepository;
import com.foodapp.food_ordering_backend.service.MenuCategoryService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MenuCategoryServiceImpl implements MenuCategoryService {

    private static final Logger log =
            LoggerFactory.getLogger(MenuCategoryServiceImpl.class);
    private final MenuCategoryRepository menuCategoryRepository;
    private final RestaurantRepository restaurantRepository;
    private final MenuCategoryMapper menuCategoryMapper;

    @Override
    public MenuCategoryResponse createCategory(MenuCategoryRequest request) {

        log.info(
                "Creating menu category '{}' for restaurant ID={}",
                request.getName(),
                request.getRestaurantId()
        );
        Restaurant restaurant = restaurantRepository.findById(request.getRestaurantId())
                .orElseThrow(() -> {

                    log.warn(
                            "Menu category creation failed. Restaurant not found. ID={}",
                            request.getRestaurantId()
                    );

                    return new ResourceNotFoundException("Restaurant not found");
                });

        if (menuCategoryRepository.existsByRestaurantIdAndNameIgnoreCase(
                restaurant.getId(),
                request.getName())) {

            log.warn(
                    "Menu category '{}' already exists for restaurant ID={}",
                    request.getName(),
                    request.getRestaurantId()
            );
            throw new BadRequestException("Category already exists for this restaurant");
        }

        MenuCategory category =
                menuCategoryMapper.toEntity(request, restaurant);

        category = menuCategoryRepository.save(category);
        log.info(
                "Menu category created successfully. ID={}, Name={}",
                category.getId(),
                category.getName()
        );

        return menuCategoryMapper.toResponse(category);
    }

    @Override
    public MenuCategoryResponse updateCategory(
            Long id,
            MenuCategoryRequest request) {

        log.info("Updating menu category ID={}", id);

        MenuCategory category = menuCategoryRepository.findById(id)
                .orElseThrow(() -> {

                    log.warn(
                            "Menu category not found. ID={}",
                            id
                    );

                    return new ResourceNotFoundException("Category not found");
                });

        Restaurant restaurant = restaurantRepository.findById(request.getRestaurantId())
                .orElseThrow(() -> {

                    log.warn(
                            "Restaurant not found. ID={}",
                            request.getRestaurantId()
                    );

                    return new ResourceNotFoundException("Restaurant not found");
                });

        if (!category.getName().equalsIgnoreCase(request.getName())
                && menuCategoryRepository.existsByRestaurantIdAndNameIgnoreCase(
                restaurant.getId(),
                request.getName())) {

            log.warn(
                    "Menu category update failed. Category '{}' already exists for restaurant ID={}",
                    request.getName(),
                    restaurant.getId()
            );

            throw new BadRequestException("Category already exists for this restaurant");
        }

        menuCategoryMapper.updateEntity(
                category,
                request,
                restaurant
        );

        category = menuCategoryRepository.save(category);

        log.info(
                "Menu category updated successfully. ID={}, Name={}",
                category.getId(),
                category.getName()
        );

        return menuCategoryMapper.toResponse(category);
    }

    @Override
    public void deleteCategory(Long id) {

        log.info("Deleting menu category ID={}", id);

        MenuCategory category = menuCategoryRepository.findById(id)
                .orElseThrow(() -> {

                    log.warn(
                            "Menu category not found. ID={}",
                            id
                    );

                    return new ResourceNotFoundException("Category not found");
                });

        log.info(
                "Menu category deleted successfully. ID={}, Name={}",
                category.getId(),
                category.getName()
        );

        menuCategoryRepository.delete(category);
    }

    @Override
    public MenuCategoryResponse getCategoryById(Long id) {

        log.info("Fetching menu category ID={}", id);

        MenuCategory category = menuCategoryRepository.findById(id)
                .orElseThrow(() -> {

                    log.warn(
                            "Menu category not found. ID={}",
                            id
                    );

                    return new ResourceNotFoundException("Category not found");
                });

        log.info(
                "Menu category fetched successfully. ID={}, Name={}",
                category.getId(),
                category.getName()
        );

        return menuCategoryMapper.toResponse(category);
    }

    @Override
    public List<MenuCategoryResponse> getCategoriesByRestaurant(Long restaurantId) {

        log.info(
                "Fetching menu categories for restaurant ID={}",
                restaurantId
        );

        if (!restaurantRepository.existsById(restaurantId)) {

            log.warn(
                    "Restaurant not found. ID={}",
                    restaurantId
            );

            throw new ResourceNotFoundException("Restaurant not found");
        }

        List<MenuCategoryResponse> response = menuCategoryRepository
                .findByRestaurantId(restaurantId)
                .stream()
                .map(menuCategoryMapper::toResponse)
                .toList();

        log.info(
                "Fetched {} menu categories for restaurant ID={}",
                response.size(),
                restaurantId
        );

        return response;
    }
}