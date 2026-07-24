package com.foodapp.food_ordering_backend.service;

import com.foodapp.food_ordering_backend.dto.request.RestaurantRequest;
import com.foodapp.food_ordering_backend.dto.response.RestaurantResponse;
import org.springframework.data.domain.Page;
import java.util.List;

public interface RestaurantService {

    RestaurantResponse createRestaurant(RestaurantRequest request);

    RestaurantResponse updateRestaurant(Long id, RestaurantRequest request);

    void deleteRestaurant(Long id);

    RestaurantResponse getRestaurantById(Long id);

    List<RestaurantResponse> getAllRestaurants();

    Page<RestaurantResponse> getRestaurants(
            String keyword,
            String city,
            String state,
            Boolean open,
            Double minRating,
            int page,
            int size,
            String sortBy,
            String direction);
}