package com.foodapp.food_ordering_backend.service.impl;

import com.foodapp.food_ordering_backend.exception.BadRequestException;
import com.foodapp.food_ordering_backend.exception.ResourceNotFoundException;
import com.foodapp.food_ordering_backend.mapper.RestaurantMapper;
import com.foodapp.food_ordering_backend.repository.RestaurantRepository;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.foodapp.food_ordering_backend.dto.request.RestaurantRequest;
import com.foodapp.food_ordering_backend.dto.response.RestaurantResponse;
import com.foodapp.food_ordering_backend.entity.Restaurant;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RestaurantServiceImplTest {

    @Mock
    private RestaurantRepository restaurantRepository;

    @Mock
    private RestaurantMapper restaurantMapper;

    @InjectMocks
    private RestaurantServiceImpl restaurantService;


    // =========================
    // CREATE RESTAURANT TESTS
    // =========================

    @Test
    void createRestaurant_shouldCreateSuccessfully() {

        // Arrange
        RestaurantRequest request = new RestaurantRequest();
        request.setName("Food Palace");
        request.setEmail("foodpalace@gmail.com");
        request.setPhoneNumber("9876543210");

        Restaurant restaurant = new Restaurant();
        restaurant.setId(1L);
        restaurant.setName("Food Palace");

        RestaurantResponse response = new RestaurantResponse();
        response.setId(1L);
        response.setName("Food Palace");

        when(restaurantRepository.existsByEmail(request.getEmail()))
                .thenReturn(false);

        when(restaurantRepository.existsByPhoneNumber(request.getPhoneNumber()))
                .thenReturn(false);

        when(restaurantMapper.toRestaurant(request))
                .thenReturn(restaurant);

        when(restaurantRepository.save(restaurant))
                .thenReturn(restaurant);

        when(restaurantMapper.toRestaurantResponse(restaurant))
                .thenReturn(response);

        // Act
        RestaurantResponse result =
                restaurantService.createRestaurant(request);

        // Assert
        assertEquals(1L, result.getId());
        assertEquals("Food Palace", result.getName());

        verify(restaurantRepository)
                .existsByEmail(request.getEmail());

        verify(restaurantRepository)
                .existsByPhoneNumber(request.getPhoneNumber());

        verify(restaurantRepository)
                .save(restaurant);

        verify(restaurantMapper)
                .toRestaurant(request);

        verify(restaurantMapper)
                .toRestaurantResponse(restaurant);
    }

    @Test
    void createRestaurant_shouldThrowException_whenEmailAlreadyExists() {

        // Arrange
        RestaurantRequest request = new RestaurantRequest();
        request.setName("Food Palace");
        request.setEmail("foodpalace@gmail.com");
        request.setPhoneNumber("9876543210");

        when(restaurantRepository.existsByEmail(request.getEmail()))
                .thenReturn(true);

        // Act + Assert
        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> restaurantService.createRestaurant(request)
        );

        // Verify exception message
        assertEquals(
                "Restaurant email already exists",
                exception.getMessage()
        );

        // Verify that execution stopped here
        verify(restaurantRepository)
                .existsByEmail(request.getEmail());

        verify(restaurantRepository, never())
                .existsByPhoneNumber(anyString());

        verify(restaurantRepository, never())
                .save(any(Restaurant.class));

        verify(restaurantMapper, never())
                .toRestaurant(any(RestaurantRequest.class));
    }

    @Test
    void createRestaurant_shouldThrowException_whenPhoneNumberAlreadyExists() {

        // Arrange
        RestaurantRequest request = new RestaurantRequest();
        request.setName("Food Palace");
        request.setEmail("foodpalace@gmail.com");
        request.setPhoneNumber("9876543210");

        // Email is unique
        when(restaurantRepository.existsByEmail(request.getEmail()))
                .thenReturn(false);

        // Phone number already exists
        when(restaurantRepository.existsByPhoneNumber(request.getPhoneNumber()))
                .thenReturn(true);

        // Act + Assert
        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> restaurantService.createRestaurant(request)
        );

        // Verify exception message
        assertEquals(
                "Restaurant phone number already exists",
                exception.getMessage()
        );

        // Verify repository calls
        verify(restaurantRepository)
                .existsByEmail(request.getEmail());

        verify(restaurantRepository)
                .existsByPhoneNumber(request.getPhoneNumber());

        // Restaurant should NOT be saved
        verify(restaurantRepository, never())
                .save(any(Restaurant.class));

        verify(restaurantMapper, never())
                .toRestaurant(any(RestaurantRequest.class));
    }


    // =========================
    // UPDATE RESTAURANT TESTS
    // =========================

    @Test
    void updateRestaurant_shouldUpdateSuccessfully() {

        // Arrange
        Long restaurantId = 1L;

        RestaurantRequest request = new RestaurantRequest();
        request.setName("Updated Food Palace");
        request.setEmail("updated@gmail.com");
        request.setPhoneNumber("9999999999");

        Restaurant existingRestaurant = new Restaurant();
        existingRestaurant.setId(restaurantId);
        existingRestaurant.setName("Food Palace");
        existingRestaurant.setEmail("old@gmail.com");
        existingRestaurant.setPhoneNumber("8888888888");

        RestaurantResponse response = new RestaurantResponse();
        response.setId(restaurantId);
        response.setName("Updated Food Palace");

        // Restaurant exists
        when(restaurantRepository.findById(restaurantId))
                .thenReturn(Optional.of(existingRestaurant));

        // New email and phone are unique
        when(restaurantRepository.existsByEmail(request.getEmail()))
                .thenReturn(false);

        when(restaurantRepository.existsByPhoneNumber(request.getPhoneNumber()))
                .thenReturn(false);

        // Mapper updates the existing entity
        doNothing()
                .when(restaurantMapper)
                .updateRestaurant(existingRestaurant, request);

        when(restaurantRepository.save(existingRestaurant))
                .thenReturn(existingRestaurant);

        when(restaurantMapper.toRestaurantResponse(existingRestaurant))
                .thenReturn(response);

        // Act
        RestaurantResponse result =
                restaurantService.updateRestaurant(
                        restaurantId,
                        request
                );

        // Assert
        assertEquals(restaurantId, result.getId());
        assertEquals("Updated Food Palace", result.getName());

        // Verify repository interactions
        verify(restaurantRepository)
                .findById(restaurantId);

        verify(restaurantRepository)
                .existsByEmail(request.getEmail());

        verify(restaurantRepository)
                .existsByPhoneNumber(request.getPhoneNumber());

        verify(restaurantMapper)
                .updateRestaurant(existingRestaurant, request);

        verify(restaurantRepository)
                .save(existingRestaurant);

        verify(restaurantMapper)
                .toRestaurantResponse(existingRestaurant);
    }

    @Test
    void updateRestaurant_shouldThrowException_whenRestaurantNotFound() {

        // Arrange
        Long restaurantId = 999L;

        RestaurantRequest request = new RestaurantRequest();
        request.setName("Updated Food Palace");
        request.setEmail("updated@gmail.com");
        request.setPhoneNumber("9999999999");

        when(restaurantRepository.findById(restaurantId))
                .thenReturn(Optional.empty());

        // Act + Assert
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> restaurantService.updateRestaurant(
                        restaurantId,
                        request
                )
        );

        // Verify exception message
        assertEquals(
                "Restaurant not found",
                exception.getMessage()
        );

        // Verify that the repository was checked
        verify(restaurantRepository)
                .findById(restaurantId);

        // Nothing else should happen
        verify(restaurantRepository, never())
                .existsByEmail(anyString());

        verify(restaurantRepository, never())
                .existsByPhoneNumber(anyString());

        verify(restaurantRepository, never())
                .save(any(Restaurant.class));

        verify(restaurantMapper, never())
                .updateRestaurant(
                        any(Restaurant.class),
                        any(RestaurantRequest.class)
                );
    }


             // =========================
             // DELETE RESTAURANT TESTS
             // =========================

    @Test
    void deleteRestaurant_shouldDeleteSuccessfully() {

        // Arrange
        Long restaurantId = 1L;

        Restaurant restaurant = new Restaurant();
        restaurant.setId(restaurantId);
        restaurant.setName("Food Palace");

        when(restaurantRepository.findById(restaurantId))
                .thenReturn(Optional.of(restaurant));

        // Act
        restaurantService.deleteRestaurant(restaurantId);

        // Assert
        verify(restaurantRepository)
                .findById(restaurantId);

        verify(restaurantRepository)
                .delete(restaurant);
    }

    @Test
    void deleteRestaurant_shouldThrowException_whenRestaurantNotFound() {

        // Arrange
        Long restaurantId = 999L;

        when(restaurantRepository.findById(restaurantId))
                .thenReturn(Optional.empty());

        // Act + Assert
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> restaurantService.deleteRestaurant(restaurantId)
        );

        // Verify exception message
        assertEquals(
                "Restaurant not found",
                exception.getMessage()
        );

        // Verify repository was checked
        verify(restaurantRepository)
                .findById(restaurantId);

        // Delete should NOT happen
        verify(restaurantRepository, never())
                .delete(any(Restaurant.class));
    }


    // =========================
    // GET RESTAURANT TESTS
    // =========================

    @Test
    void getRestaurantById_shouldReturnRestaurant() {

        // Arrange
        Long restaurantId = 1L;

        Restaurant restaurant = new Restaurant();
        restaurant.setId(restaurantId);
        restaurant.setName("Food Palace");

        RestaurantResponse response = new RestaurantResponse();
        response.setId(restaurantId);
        response.setName("Food Palace");

        when(restaurantRepository.findById(restaurantId))
                .thenReturn(Optional.of(restaurant));

        when(restaurantMapper.toRestaurantResponse(restaurant))
                .thenReturn(response);

        // Act
        RestaurantResponse result =
                restaurantService.getRestaurantById(restaurantId);

        // Assert
        assertEquals(restaurantId, result.getId());
        assertEquals("Food Palace", result.getName());

        verify(restaurantRepository)
                .findById(restaurantId);

        verify(restaurantMapper)
                .toRestaurantResponse(restaurant);
    }

    @Test
    void getRestaurantById_shouldThrowException_whenRestaurantNotFound() {

        // Arrange
        Long restaurantId = 999L;

        when(restaurantRepository.findById(restaurantId))
                .thenReturn(Optional.empty());

        // Act + Assert
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> restaurantService.getRestaurantById(restaurantId)
        );

        // Verify exception message
        assertEquals(
                "Restaurant not found",
                exception.getMessage()
        );

        // Verify repository was called
        verify(restaurantRepository)
                .findById(restaurantId);

        // Mapper should never be called
        verify(restaurantMapper, never())
                .toRestaurantResponse(any(Restaurant.class));
    }


    // =========================
    // GET ALL RESTAURANTS TESTS
    // =========================

    @Test
    void getAllRestaurants_shouldReturnRestaurants() {

        // Arrange
        Restaurant restaurant1 = new Restaurant();
        restaurant1.setId(1L);
        restaurant1.setName("Food Palace");

        Restaurant restaurant2 = new Restaurant();
        restaurant2.setId(2L);
        restaurant2.setName("Spice Hub");

        RestaurantResponse response1 = new RestaurantResponse();
        response1.setId(1L);
        response1.setName("Food Palace");

        RestaurantResponse response2 = new RestaurantResponse();
        response2.setId(2L);
        response2.setName("Spice Hub");

        List<Restaurant> restaurants =
                List.of(restaurant1, restaurant2);

        when(restaurantRepository.findAll())
                .thenReturn(restaurants);

        when(restaurantMapper.toRestaurantResponse(restaurant1))
                .thenReturn(response1);

        when(restaurantMapper.toRestaurantResponse(restaurant2))
                .thenReturn(response2);

        // Act
        List<RestaurantResponse> result =
                restaurantService.getAllRestaurants();

        // Assert
        assertEquals(2, result.size());

        assertEquals(1L, result.get(0).getId());
        assertEquals("Food Palace", result.get(0).getName());

        assertEquals(2L, result.get(1).getId());
        assertEquals("Spice Hub", result.get(1).getName());

        // Verify repository was called only once
        verify(restaurantRepository, times(1))
                .findAll();

        // Verify both restaurants were mapped
        verify(restaurantMapper)
                .toRestaurantResponse(restaurant1);

        verify(restaurantMapper)
                .toRestaurantResponse(restaurant2);
    }


    // =========================
    // SEARCH / FILTER TESTS
    // =========================

    @Test
    void getRestaurants_shouldReturnPagedRestaurants() {

        // Arrange
        String keyword = "Food";
        String city = "Gorakhpur";
        String state = "Uttar Pradesh";
        Boolean open = true;
        Double minRating = 4.0;

        int page = 0;
        int size = 10;
        String sortBy = "name";
        String direction = "asc";

        Restaurant restaurant = new Restaurant();
        restaurant.setId(1L);
        restaurant.setName("Food Palace");

        RestaurantResponse response = new RestaurantResponse();
        response.setId(1L);
        response.setName("Food Palace");

        Page<Restaurant> restaurantPage =
                new PageImpl<>(
                        List.of(restaurant),
                        PageRequest.of(0, 10),
                        1
                );

        when(restaurantRepository.findAll(
                any(Specification.class),
                any(Pageable.class)
        )).thenReturn(restaurantPage);

        when(restaurantMapper.toRestaurantResponse(restaurant))
                .thenReturn(response);

        // Act
        Page<RestaurantResponse> result =
                restaurantService.getRestaurants(
                        keyword,
                        city,
                        state,
                        open,
                        minRating,
                        page,
                        size,
                        sortBy,
                        direction
                );

        // Assert
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());

        assertEquals(1L, result.getContent().get(0).getId());
        assertEquals(
                "Food Palace",
                result.getContent().get(0).getName()
        );

        // Verify repository was called
        verify(restaurantRepository)
                .findAll(
                        any(Specification.class),
                        any(Pageable.class)
                );

        // Verify mapper
        verify(restaurantMapper)
                .toRestaurantResponse(restaurant);
    }
}