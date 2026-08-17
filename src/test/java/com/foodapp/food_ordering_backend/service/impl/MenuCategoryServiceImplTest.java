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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MenuCategoryServiceImplTest {

    @Mock
    private MenuCategoryRepository menuCategoryRepository;

    @Mock
    private RestaurantRepository restaurantRepository;

    @Mock
    private MenuCategoryMapper menuCategoryMapper;

    @InjectMocks
    private MenuCategoryServiceImpl menuCategoryService;


    // =====================================================
    // CREATE CATEGORY TESTS
    // =====================================================

    @Test
    void createCategory_shouldCreateSuccessfully() {

        // Arrange
        MenuCategoryRequest request = new MenuCategoryRequest();
        request.setName("Main Course");
        request.setRestaurantId(1L);

        Restaurant restaurant = new Restaurant();
        restaurant.setId(1L);
        restaurant.setName("Food Palace");

        MenuCategory category = new MenuCategory();
        category.setId(10L);
        category.setName("Main Course");
        category.setRestaurant(restaurant);

        MenuCategoryResponse response = new MenuCategoryResponse();
        response.setId(10L);
        response.setName("Main Course");

        when(restaurantRepository.findById(1L))
                .thenReturn(Optional.of(restaurant));

        when(menuCategoryRepository
                .existsByRestaurantIdAndNameIgnoreCase(
                        1L,
                        "Main Course"))
                .thenReturn(false);

        when(menuCategoryMapper.toEntity(request, restaurant))
                .thenReturn(category);

        when(menuCategoryRepository.save(category))
                .thenReturn(category);

        when(menuCategoryMapper.toResponse(category))
                .thenReturn(response);

        // Act
        MenuCategoryResponse result =
                menuCategoryService.createCategory(request);

        // Assert
        assertNotNull(result);
        assertEquals(10L, result.getId());
        assertEquals("Main Course", result.getName());

        verify(restaurantRepository)
                .findById(1L);

        verify(menuCategoryRepository)
                .existsByRestaurantIdAndNameIgnoreCase(
                        1L,
                        "Main Course");

        verify(menuCategoryMapper)
                .toEntity(request, restaurant);

        verify(menuCategoryRepository)
                .save(category);

        verify(menuCategoryMapper)
                .toResponse(category);
    }


    @Test
    void createCategory_shouldThrowException_whenRestaurantNotFound() {

        // Arrange
        MenuCategoryRequest request = new MenuCategoryRequest();
        request.setName("Main Course");
        request.setRestaurantId(999L);

        when(restaurantRepository.findById(999L))
                .thenReturn(Optional.empty());

        // Act + Assert
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> menuCategoryService.createCategory(request)
        );

        assertEquals(
                "Restaurant not found",
                exception.getMessage()
        );

        verify(restaurantRepository)
                .findById(999L);

        verify(menuCategoryRepository, never())
                .existsByRestaurantIdAndNameIgnoreCase(
                        anyLong(),
                        anyString());

        verify(menuCategoryRepository, never())
                .save(any(MenuCategory.class));
    }


    @Test
    void createCategory_shouldThrowException_whenCategoryAlreadyExists() {

        // Arrange
        MenuCategoryRequest request = new MenuCategoryRequest();
        request.setName("Main Course");
        request.setRestaurantId(1L);

        Restaurant restaurant = new Restaurant();
        restaurant.setId(1L);

        when(restaurantRepository.findById(1L))
                .thenReturn(Optional.of(restaurant));

        when(menuCategoryRepository
                .existsByRestaurantIdAndNameIgnoreCase(
                        1L,
                        "Main Course"))
                .thenReturn(true);

        // Act + Assert
        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> menuCategoryService.createCategory(request)
        );

        assertEquals(
                "Category already exists for this restaurant",
                exception.getMessage()
        );

        verify(menuCategoryRepository)
                .existsByRestaurantIdAndNameIgnoreCase(
                        1L,
                        "Main Course");

        verify(menuCategoryMapper, never())
                .toEntity(
                        any(MenuCategoryRequest.class),
                        any(Restaurant.class));

        verify(menuCategoryRepository, never())
                .save(any(MenuCategory.class));
    }


    // =====================================================
    // UPDATE CATEGORY TESTS
    // =====================================================

    @Test
    void updateCategory_shouldUpdateSuccessfully() {

        // Arrange
        Long categoryId = 10L;

        MenuCategoryRequest request = new MenuCategoryRequest();
        request.setName("Updated Category");
        request.setRestaurantId(1L);

        Restaurant restaurant = new Restaurant();
        restaurant.setId(1L);

        MenuCategory category = new MenuCategory();
        category.setId(categoryId);
        category.setName("Old Category");
        category.setRestaurant(restaurant);

        MenuCategoryResponse response = new MenuCategoryResponse();
        response.setId(categoryId);
        response.setName("Updated Category");

        when(menuCategoryRepository.findById(categoryId))
                .thenReturn(Optional.of(category));

        when(restaurantRepository.findById(1L))
                .thenReturn(Optional.of(restaurant));

        when(menuCategoryRepository
                .existsByRestaurantIdAndNameIgnoreCase(
                        1L,
                        "Updated Category"))
                .thenReturn(false);

        doNothing()
                .when(menuCategoryMapper)
                .updateEntity(
                        category,
                        request,
                        restaurant);

        when(menuCategoryRepository.save(category))
                .thenReturn(category);

        when(menuCategoryMapper.toResponse(category))
                .thenReturn(response);

        // Act
        MenuCategoryResponse result =
                menuCategoryService.updateCategory(
                        categoryId,
                        request);

        // Assert
        assertNotNull(result);
        assertEquals(categoryId, result.getId());
        assertEquals("Updated Category", result.getName());

        verify(menuCategoryRepository)
                .findById(categoryId);

        verify(restaurantRepository)
                .findById(1L);

        verify(menuCategoryMapper)
                .updateEntity(
                        category,
                        request,
                        restaurant);

        verify(menuCategoryRepository)
                .save(category);

        verify(menuCategoryMapper)
                .toResponse(category);
    }


    @Test
    void updateCategory_shouldThrowException_whenCategoryNotFound() {

        // Arrange
        Long categoryId = 999L;

        MenuCategoryRequest request = new MenuCategoryRequest();
        request.setName("Updated Category");
        request.setRestaurantId(1L);

        when(menuCategoryRepository.findById(categoryId))
                .thenReturn(Optional.empty());

        // Act + Assert
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> menuCategoryService.updateCategory(
                        categoryId,
                        request)
        );

        assertEquals(
                "Category not found",
                exception.getMessage()
        );

        verify(menuCategoryRepository)
                .findById(categoryId);

        verify(restaurantRepository, never())
                .findById(anyLong());

        verify(menuCategoryRepository, never())
                .save(any(MenuCategory.class));
    }


    @Test
    void updateCategory_shouldThrowException_whenRestaurantNotFound() {

        // Arrange
        Long categoryId = 10L;

        MenuCategoryRequest request = new MenuCategoryRequest();
        request.setName("Updated Category");
        request.setRestaurantId(999L);

        MenuCategory category = new MenuCategory();
        category.setId(categoryId);
        category.setName("Old Category");

        when(menuCategoryRepository.findById(categoryId))
                .thenReturn(Optional.of(category));

        when(restaurantRepository.findById(999L))
                .thenReturn(Optional.empty());

        // Act + Assert
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> menuCategoryService.updateCategory(
                        categoryId,
                        request)
        );

        assertEquals(
                "Restaurant not found",
                exception.getMessage()
        );

        verify(menuCategoryRepository)
                .findById(categoryId);

        verify(restaurantRepository)
                .findById(999L);

        verify(menuCategoryRepository, never())
                .save(any(MenuCategory.class));
    }


    @Test
    void updateCategory_shouldThrowException_whenCategoryNameAlreadyExists() {

        // Arrange
        Long categoryId = 10L;

        MenuCategoryRequest request = new MenuCategoryRequest();
        request.setName("Desserts");
        request.setRestaurantId(1L);

        Restaurant restaurant = new Restaurant();
        restaurant.setId(1L);

        MenuCategory category = new MenuCategory();
        category.setId(categoryId);
        category.setName("Main Course");

        when(menuCategoryRepository.findById(categoryId))
                .thenReturn(Optional.of(category));

        when(restaurantRepository.findById(1L))
                .thenReturn(Optional.of(restaurant));

        when(menuCategoryRepository
                .existsByRestaurantIdAndNameIgnoreCase(
                        1L,
                        "Desserts"))
                .thenReturn(true);

        // Act + Assert
        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> menuCategoryService.updateCategory(
                        categoryId,
                        request)
        );

        assertEquals(
                "Category already exists for this restaurant",
                exception.getMessage()
        );

        verify(menuCategoryRepository)
                .existsByRestaurantIdAndNameIgnoreCase(
                        1L,
                        "Desserts");

        verify(menuCategoryRepository, never())
                .save(any(MenuCategory.class));

        verify(menuCategoryMapper, never())
                .updateEntity(
                        any(MenuCategory.class),
                        any(MenuCategoryRequest.class),
                        any(Restaurant.class));
    }


    // =====================================================
    // DELETE CATEGORY TESTS
    // =====================================================

    @Test
    void deleteCategory_shouldDeleteSuccessfully() {

        // Arrange
        Long categoryId = 10L;

        MenuCategory category = new MenuCategory();
        category.setId(categoryId);
        category.setName("Main Course");

        when(menuCategoryRepository.findById(categoryId))
                .thenReturn(Optional.of(category));

        // Act
        menuCategoryService.deleteCategory(categoryId);

        // Assert
        verify(menuCategoryRepository)
                .findById(categoryId);

        verify(menuCategoryRepository)
                .delete(category);
    }


    @Test
    void deleteCategory_shouldThrowException_whenCategoryNotFound() {

        // Arrange
        Long categoryId = 999L;

        when(menuCategoryRepository.findById(categoryId))
                .thenReturn(Optional.empty());

        // Act + Assert
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> menuCategoryService.deleteCategory(categoryId)
        );

        assertEquals(
                "Category not found",
                exception.getMessage()
        );

        verify(menuCategoryRepository)
                .findById(categoryId);

        verify(menuCategoryRepository, never())
                .delete(any(MenuCategory.class));
    }


    // =====================================================
    // GET CATEGORY BY ID TESTS
    // =====================================================

    @Test
    void getCategoryById_shouldReturnCategory() {

        // Arrange
        Long categoryId = 10L;

        MenuCategory category = new MenuCategory();
        category.setId(categoryId);
        category.setName("Main Course");

        MenuCategoryResponse response = new MenuCategoryResponse();
        response.setId(categoryId);
        response.setName("Main Course");

        when(menuCategoryRepository.findById(categoryId))
                .thenReturn(Optional.of(category));

        when(menuCategoryMapper.toResponse(category))
                .thenReturn(response);

        // Act
        MenuCategoryResponse result =
                menuCategoryService.getCategoryById(categoryId);

        // Assert
        assertNotNull(result);
        assertEquals(categoryId, result.getId());
        assertEquals("Main Course", result.getName());

        verify(menuCategoryRepository)
                .findById(categoryId);

        verify(menuCategoryMapper)
                .toResponse(category);
    }


    @Test
    void getCategoryById_shouldThrowException_whenCategoryNotFound() {

        // Arrange
        Long categoryId = 999L;

        when(menuCategoryRepository.findById(categoryId))
                .thenReturn(Optional.empty());

        // Act + Assert
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> menuCategoryService.getCategoryById(categoryId)
        );

        assertEquals(
                "Category not found",
                exception.getMessage()
        );

        verify(menuCategoryRepository)
                .findById(categoryId);

        verify(menuCategoryMapper, never())
                .toResponse(any(MenuCategory.class));
    }


    // =====================================================
    // GET CATEGORIES BY RESTAURANT TESTS
    // =====================================================

    @Test
    void getCategoriesByRestaurant_shouldReturnCategories() {

        // Arrange
        Long restaurantId = 1L;

        MenuCategory category1 = new MenuCategory();
        category1.setId(10L);
        category1.setName("Main Course");

        MenuCategory category2 = new MenuCategory();
        category2.setId(11L);
        category2.setName("Desserts");

        MenuCategoryResponse response1 = new MenuCategoryResponse();
        response1.setId(10L);
        response1.setName("Main Course");

        MenuCategoryResponse response2 = new MenuCategoryResponse();
        response2.setId(11L);
        response2.setName("Desserts");

        when(restaurantRepository.existsById(restaurantId))
                .thenReturn(true);

        when(menuCategoryRepository.findByRestaurantId(restaurantId))
                .thenReturn(List.of(category1, category2));

        when(menuCategoryMapper.toResponse(category1))
                .thenReturn(response1);

        when(menuCategoryMapper.toResponse(category2))
                .thenReturn(response2);

        // Act
        List<MenuCategoryResponse> result =
                menuCategoryService.getCategoriesByRestaurant(
                        restaurantId);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());

        assertEquals(10L, result.get(0).getId());
        assertEquals("Main Course", result.get(0).getName());

        assertEquals(11L, result.get(1).getId());
        assertEquals("Desserts", result.get(1).getName());

        verify(restaurantRepository)
                .existsById(restaurantId);

        verify(menuCategoryRepository)
                .findByRestaurantId(restaurantId);

        verify(menuCategoryMapper)
                .toResponse(category1);

        verify(menuCategoryMapper)
                .toResponse(category2);
    }


    @Test
    void getCategoriesByRestaurant_shouldThrowException_whenRestaurantNotFound() {

        // Arrange
        Long restaurantId = 999L;

        when(restaurantRepository.existsById(restaurantId))
                .thenReturn(false);

        // Act + Assert
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> menuCategoryService.getCategoriesByRestaurant(
                        restaurantId)
        );

        assertEquals(
                "Restaurant not found",
                exception.getMessage()
        );

        verify(restaurantRepository)
                .existsById(restaurantId);

        verify(menuCategoryRepository, never())
                .findByRestaurantId(anyLong());

        verify(menuCategoryMapper, never())
                .toResponse(any(MenuCategory.class));
    }


    @Test
    void getCategoriesByRestaurant_shouldReturnEmptyList_whenNoCategoriesExist() {

        // Arrange
        Long restaurantId = 1L;

        when(restaurantRepository.existsById(restaurantId))
                .thenReturn(true);

        when(menuCategoryRepository.findByRestaurantId(restaurantId))
                .thenReturn(List.of());

        // Act
        List<MenuCategoryResponse> result =
                menuCategoryService.getCategoriesByRestaurant(
                        restaurantId);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(restaurantRepository)
                .existsById(restaurantId);

        verify(menuCategoryRepository)
                .findByRestaurantId(restaurantId);

        verifyNoInteractions(menuCategoryMapper);
    }
}