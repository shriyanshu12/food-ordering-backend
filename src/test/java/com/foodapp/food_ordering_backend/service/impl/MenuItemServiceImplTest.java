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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MenuItemServiceImplTest {

    @Mock
    private MenuItemRepository menuItemRepository;

    @Mock
    private RestaurantRepository restaurantRepository;

    @Mock
    private MenuCategoryRepository menuCategoryRepository;

    @Mock
    private MenuItemMapper menuItemMapper;

    @InjectMocks
    private MenuItemServiceImpl menuItemService;


    // =====================================================
    // CREATE MENU ITEM TESTS
    // =====================================================

    @Test
    void createMenuItem_shouldCreateSuccessfully() {

        MenuItemRequest request = new MenuItemRequest();
        request.setName("Paneer Tikka");
        request.setRestaurantId(1L);
        request.setCategoryId(10L);
        request.setPrice(BigDecimal.valueOf(250));

        Restaurant restaurant = new Restaurant();
        restaurant.setId(1L);
        restaurant.setName("Food Palace");

        MenuCategory category = new MenuCategory();
        category.setId(10L);
        category.setName("Starters");

        MenuItem item = new MenuItem();
        item.setId(100L);
        item.setName("Paneer Tikka");
        item.setRestaurant(restaurant);
        item.setCategory(category);
        item.setPrice(BigDecimal.valueOf(250));

        MenuItemResponse response = new MenuItemResponse();
        response.setId(100L);
        response.setName("Paneer Tikka");

        when(restaurantRepository.findById(1L))
                .thenReturn(Optional.of(restaurant));

        when(menuCategoryRepository.findById(10L))
                .thenReturn(Optional.of(category));

        when(menuCategoryRepository.existsByIdAndRestaurantId(10L, 1L))
                .thenReturn(true);

        when(menuItemRepository
                .existsByRestaurantIdAndCategoryIdAndNameIgnoreCase(
                        1L,
                        10L,
                        "Paneer Tikka"))
                .thenReturn(false);

        when(menuItemMapper.toEntity(request, restaurant, category))
                .thenReturn(item);

        when(menuItemRepository.save(item))
                .thenReturn(item);

        when(menuItemMapper.toResponse(item))
                .thenReturn(response);

        MenuItemResponse result =
                menuItemService.createMenuItem(request);

        assertNotNull(result);
        assertEquals(100L, result.getId());
        assertEquals("Paneer Tikka", result.getName());

        verify(restaurantRepository).findById(1L);
        verify(menuCategoryRepository).findById(10L);

        verify(menuCategoryRepository)
                .existsByIdAndRestaurantId(10L, 1L);

        verify(menuItemRepository)
                .existsByRestaurantIdAndCategoryIdAndNameIgnoreCase(
                        1L,
                        10L,
                        "Paneer Tikka");

        verify(menuItemRepository).save(item);
        verify(menuItemMapper).toResponse(item);
    }


    @Test
    void createMenuItem_shouldThrowException_whenRestaurantNotFound() {

        MenuItemRequest request = new MenuItemRequest();
        request.setName("Paneer Tikka");
        request.setRestaurantId(999L);
        request.setCategoryId(10L);

        when(restaurantRepository.findById(999L))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> menuItemService.createMenuItem(request)
        );

        assertEquals(
                "Restaurant not found",
                exception.getMessage()
        );

        verify(restaurantRepository).findById(999L);

        verify(menuCategoryRepository, never())
                .findById(anyLong());

        verify(menuItemRepository, never())
                .save(any(MenuItem.class));
    }


    @Test
    void createMenuItem_shouldThrowException_whenCategoryNotFound() {

        MenuItemRequest request = new MenuItemRequest();
        request.setName("Paneer Tikka");
        request.setRestaurantId(1L);
        request.setCategoryId(999L);

        Restaurant restaurant = new Restaurant();
        restaurant.setId(1L);

        when(restaurantRepository.findById(1L))
                .thenReturn(Optional.of(restaurant));

        when(menuCategoryRepository.findById(999L))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> menuItemService.createMenuItem(request)
        );

        assertEquals(
                "Category not found",
                exception.getMessage()
        );

        verify(menuCategoryRepository).findById(999L);

        verify(menuItemRepository, never())
                .save(any(MenuItem.class));
    }


    @Test
    void createMenuItem_shouldThrowException_whenCategoryDoesNotBelongToRestaurant() {

        MenuItemRequest request = new MenuItemRequest();
        request.setName("Paneer Tikka");
        request.setRestaurantId(1L);
        request.setCategoryId(10L);

        Restaurant restaurant = new Restaurant();
        restaurant.setId(1L);

        MenuCategory category = new MenuCategory();
        category.setId(10L);

        when(restaurantRepository.findById(1L))
                .thenReturn(Optional.of(restaurant));

        when(menuCategoryRepository.findById(10L))
                .thenReturn(Optional.of(category));

        when(menuCategoryRepository.existsByIdAndRestaurantId(10L, 1L))
                .thenReturn(false);

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> menuItemService.createMenuItem(request)
        );

        assertEquals(
                "Selected category does not belong to the selected restaurant",
                exception.getMessage()
        );

        verify(menuItemRepository, never())
                .save(any(MenuItem.class));
    }


    @Test
    void createMenuItem_shouldThrowException_whenItemAlreadyExists() {

        MenuItemRequest request = new MenuItemRequest();
        request.setName("Paneer Tikka");
        request.setRestaurantId(1L);
        request.setCategoryId(10L);

        Restaurant restaurant = new Restaurant();
        restaurant.setId(1L);

        MenuCategory category = new MenuCategory();
        category.setId(10L);

        when(restaurantRepository.findById(1L))
                .thenReturn(Optional.of(restaurant));

        when(menuCategoryRepository.findById(10L))
                .thenReturn(Optional.of(category));

        when(menuCategoryRepository.existsByIdAndRestaurantId(10L, 1L))
                .thenReturn(true);

        when(menuItemRepository
                .existsByRestaurantIdAndCategoryIdAndNameIgnoreCase(
                        1L,
                        10L,
                        "Paneer Tikka"))
                .thenReturn(true);

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> menuItemService.createMenuItem(request)
        );

        assertEquals(
                "Menu item already exists in this category",
                exception.getMessage()
        );

        verify(menuItemRepository, never())
                .save(any(MenuItem.class));

        verify(menuItemMapper, never())
                .toEntity(
                        any(MenuItemRequest.class),
                        any(Restaurant.class),
                        any(MenuCategory.class));
    }


    // =====================================================
    // UPDATE MENU ITEM TESTS
    // =====================================================

    @Test
    void updateMenuItem_shouldUpdateSuccessfully() {

        Long itemId = 100L;

        MenuItemRequest request = new MenuItemRequest();
        request.setName("Updated Paneer Tikka");
        request.setRestaurantId(1L);
        request.setCategoryId(10L);

        Restaurant restaurant = new Restaurant();
        restaurant.setId(1L);

        MenuCategory category = new MenuCategory();
        category.setId(10L);

        MenuCategory oldCategory = new MenuCategory();
        oldCategory.setId(10L);

        MenuItem item = new MenuItem();
        item.setId(itemId);
        item.setName("Paneer Tikka");
        item.setCategory(oldCategory);

        MenuItemResponse response = new MenuItemResponse();
        response.setId(itemId);
        response.setName("Updated Paneer Tikka");

        when(menuItemRepository.findById(itemId))
                .thenReturn(Optional.of(item));

        when(restaurantRepository.findById(1L))
                .thenReturn(Optional.of(restaurant));

        when(menuCategoryRepository.findById(10L))
                .thenReturn(Optional.of(category));

        when(menuCategoryRepository.existsByIdAndRestaurantId(10L, 1L))
                .thenReturn(true);

        when(menuItemRepository
                .existsByRestaurantIdAndCategoryIdAndNameIgnoreCase(
                        1L,
                        10L,
                        "Updated Paneer Tikka"))
                .thenReturn(false);

        doNothing()
                .when(menuItemMapper)
                .updateEntity(
                        item,
                        request,
                        restaurant,
                        category);

        when(menuItemRepository.save(item))
                .thenReturn(item);

        when(menuItemMapper.toResponse(item))
                .thenReturn(response);

        MenuItemResponse result =
                menuItemService.updateMenuItem(itemId, request);

        assertNotNull(result);
        assertEquals(itemId, result.getId());
        assertEquals(
                "Updated Paneer Tikka",
                result.getName()
        );

        verify(menuItemRepository).findById(itemId);
        verify(restaurantRepository).findById(1L);
        verify(menuCategoryRepository).findById(10L);

        verify(menuItemRepository).save(item);

        verify(menuItemMapper)
                .updateEntity(
                        item,
                        request,
                        restaurant,
                        category);

        verify(menuItemMapper).toResponse(item);
    }


    @Test
    void updateMenuItem_shouldThrowException_whenItemNotFound() {

        Long itemId = 999L;

        MenuItemRequest request = new MenuItemRequest();
        request.setName("Updated Item");
        request.setRestaurantId(1L);
        request.setCategoryId(10L);

        when(menuItemRepository.findById(itemId))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> menuItemService.updateMenuItem(
                        itemId,
                        request)
        );

        assertEquals(
                "Menu item not found",
                exception.getMessage()
        );

        verify(menuItemRepository).findById(itemId);

        verify(restaurantRepository, never())
                .findById(anyLong());

        verify(menuItemRepository, never())
                .save(any(MenuItem.class));
    }


    @Test
    void updateMenuItem_shouldThrowException_whenRestaurantNotFound() {

        Long itemId = 100L;

        MenuItemRequest request = new MenuItemRequest();
        request.setName("Updated Item");
        request.setRestaurantId(999L);
        request.setCategoryId(10L);

        MenuItem item = new MenuItem();
        item.setId(itemId);
        item.setName("Old Item");

        when(menuItemRepository.findById(itemId))
                .thenReturn(Optional.of(item));

        when(restaurantRepository.findById(999L))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> menuItemService.updateMenuItem(
                        itemId,
                        request)
        );

        assertEquals(
                "Restaurant not found",
                exception.getMessage()
        );

        verify(menuItemRepository).findById(itemId);
        verify(restaurantRepository).findById(999L);

        verify(menuItemRepository, never())
                .save(any(MenuItem.class));
    }


    @Test
    void updateMenuItem_shouldThrowException_whenCategoryNotFound() {

        Long itemId = 100L;

        MenuItemRequest request = new MenuItemRequest();
        request.setName("Updated Item");
        request.setRestaurantId(1L);
        request.setCategoryId(999L);

        MenuItem item = new MenuItem();
        item.setId(itemId);
        item.setName("Old Item");

        Restaurant restaurant = new Restaurant();
        restaurant.setId(1L);

        when(menuItemRepository.findById(itemId))
                .thenReturn(Optional.of(item));

        when(restaurantRepository.findById(1L))
                .thenReturn(Optional.of(restaurant));

        when(menuCategoryRepository.findById(999L))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> menuItemService.updateMenuItem(
                        itemId,
                        request)
        );

        assertEquals(
                "Category not found",
                exception.getMessage()
        );

        verify(menuItemRepository, never())
                .save(any(MenuItem.class));
    }


    @Test
    void updateMenuItem_shouldThrowException_whenCategoryDoesNotBelongToRestaurant() {

        Long itemId = 100L;

        MenuItemRequest request = new MenuItemRequest();
        request.setName("Updated Item");
        request.setRestaurantId(1L);
        request.setCategoryId(10L);

        MenuItem item = new MenuItem();
        item.setId(itemId);
        item.setName("Old Item");

        Restaurant restaurant = new Restaurant();
        restaurant.setId(1L);

        MenuCategory category = new MenuCategory();
        category.setId(10L);

        when(menuItemRepository.findById(itemId))
                .thenReturn(Optional.of(item));

        when(restaurantRepository.findById(1L))
                .thenReturn(Optional.of(restaurant));

        when(menuCategoryRepository.findById(10L))
                .thenReturn(Optional.of(category));

        when(menuCategoryRepository.existsByIdAndRestaurantId(10L, 1L))
                .thenReturn(false);

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> menuItemService.updateMenuItem(
                        itemId,
                        request)
        );

        assertEquals(
                "Selected category does not belong to the selected restaurant",
                exception.getMessage()
        );

        verify(menuItemRepository, never())
                .save(any(MenuItem.class));
    }


    @Test
    void updateMenuItem_shouldThrowException_whenDuplicateItemExists() {

        Long itemId = 100L;

        MenuItemRequest request = new MenuItemRequest();
        request.setName("Paneer Tikka");
        request.setRestaurantId(1L);
        request.setCategoryId(10L);

        MenuCategory oldCategory = new MenuCategory();
        oldCategory.setId(10L);

        MenuItem item = new MenuItem();
        item.setId(itemId);
        item.setName("Old Paneer");
        item.setCategory(oldCategory);

        Restaurant restaurant = new Restaurant();
        restaurant.setId(1L);

        MenuCategory category = new MenuCategory();
        category.setId(10L);

        when(menuItemRepository.findById(itemId))
                .thenReturn(Optional.of(item));

        when(restaurantRepository.findById(1L))
                .thenReturn(Optional.of(restaurant));

        when(menuCategoryRepository.findById(10L))
                .thenReturn(Optional.of(category));

        when(menuCategoryRepository.existsByIdAndRestaurantId(10L, 1L))
                .thenReturn(true);

        when(menuItemRepository
                .existsByRestaurantIdAndCategoryIdAndNameIgnoreCase(
                        1L,
                        10L,
                        "Paneer Tikka"))
                .thenReturn(true);

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> menuItemService.updateMenuItem(
                        itemId,
                        request)
        );

        assertEquals(
                "Menu item already exists in this category",
                exception.getMessage()
        );

        verify(menuItemRepository, never())
                .save(any(MenuItem.class));

        verify(menuItemMapper, never())
                .updateEntity(
                        any(MenuItem.class),
                        any(MenuItemRequest.class),
                        any(Restaurant.class),
                        any(MenuCategory.class));
    }


    // =====================================================
    // DELETE MENU ITEM TESTS
    // =====================================================

    @Test
    void deleteMenuItem_shouldDeleteSuccessfully() {

        Long itemId = 100L;

        MenuItem item = new MenuItem();
        item.setId(itemId);
        item.setName("Paneer Tikka");

        when(menuItemRepository.findById(itemId))
                .thenReturn(Optional.of(item));

        menuItemService.deleteMenuItem(itemId);

        verify(menuItemRepository)
                .findById(itemId);

        verify(menuItemRepository)
                .delete(item);
    }


    @Test
    void deleteMenuItem_shouldThrowException_whenItemNotFound() {

        Long itemId = 999L;

        when(menuItemRepository.findById(itemId))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> menuItemService.deleteMenuItem(itemId)
        );

        assertEquals(
                "Menu item not found",
                exception.getMessage()
        );

        verify(menuItemRepository).findById(itemId);

        verify(menuItemRepository, never())
                .delete(any(MenuItem.class));
    }


    // =====================================================
    // GET MENU ITEM BY ID TESTS
    // =====================================================

    @Test
    void getMenuItemById_shouldReturnItem() {

        Long itemId = 100L;

        MenuItem item = new MenuItem();
        item.setId(itemId);
        item.setName("Paneer Tikka");

        MenuItemResponse response = new MenuItemResponse();
        response.setId(itemId);
        response.setName("Paneer Tikka");

        when(menuItemRepository.findById(itemId))
                .thenReturn(Optional.of(item));

        when(menuItemMapper.toResponse(item))
                .thenReturn(response);

        MenuItemResponse result =
                menuItemService.getMenuItemById(itemId);

        assertNotNull(result);
        assertEquals(itemId, result.getId());
        assertEquals("Paneer Tikka", result.getName());

        verify(menuItemRepository)
                .findById(itemId);

        verify(menuItemMapper)
                .toResponse(item);
    }


    @Test
    void getMenuItemById_shouldThrowException_whenItemNotFound() {

        Long itemId = 999L;

        when(menuItemRepository.findById(itemId))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> menuItemService.getMenuItemById(itemId)
        );

        assertEquals(
                "Menu item not found",
                exception.getMessage()
        );

        verify(menuItemRepository)
                .findById(itemId);

        verify(menuItemMapper, never())
                .toResponse(any(MenuItem.class));
    }


    // =====================================================
    // SEARCH / FILTER MENU ITEMS TEST
    // =====================================================

    @Test
    void getMenuItems_shouldReturnPagedItems() {

        MenuItem item = new MenuItem();
        item.setId(100L);
        item.setName("Paneer Tikka");

        MenuItemResponse response = new MenuItemResponse();
        response.setId(100L);
        response.setName("Paneer Tikka");

        Page<MenuItem> itemPage =
                new PageImpl<>(
                        List.of(item),
                        PageRequest.of(
                                0,
                                10,
                                Sort.by("name").ascending()
                        ),
                        1
                );

        when(menuItemRepository.findAll(
                any(Specification.class),
                any(Pageable.class)
        )).thenReturn(itemPage);

        when(menuItemMapper.toResponse(item))
                .thenReturn(response);

        Page<MenuItemResponse> result =
                menuItemService.getMenuItems(
                        "Paneer",
                        1L,
                        10L,
                        true,
                        true,
                        100.0,
                        500.0,
                        0,
                        10,
                        "name",
                        "asc"
                );

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());

        assertEquals(
                100L,
                result.getContent().get(0).getId()
        );

        assertEquals(
                "Paneer Tikka",
                result.getContent().get(0).getName()
        );

        verify(menuItemRepository)
                .findAll(
                        any(Specification.class),
                        any(Pageable.class)
                );

        verify(menuItemMapper)
                .toResponse(item);
    }
}