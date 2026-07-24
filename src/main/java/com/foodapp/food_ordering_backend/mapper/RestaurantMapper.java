package com.foodapp.food_ordering_backend.mapper;

import com.foodapp.food_ordering_backend.dto.request.RestaurantRequest;
import com.foodapp.food_ordering_backend.dto.response.RestaurantResponse;
import com.foodapp.food_ordering_backend.entity.Restaurant;
import org.springframework.stereotype.Component;

@Component
public class RestaurantMapper {

    /**
     * Convert RestaurantRequest -> Restaurant Entity
     */
    public Restaurant toRestaurant(RestaurantRequest request) {

        return Restaurant.builder()
                .name(request.getName())
                .description(request.getDescription())
                .phoneNumber(request.getPhoneNumber())
                .email(request.getEmail())
                .address(request.getAddress())
                .city(request.getCity())
                .state(request.getState())
                .zipCode(request.getZipCode())
                .imageUrl(request.getImageUrl())
                .build();
    }

    /**
     * Convert Restaurant Entity -> RestaurantResponse
     */
    public RestaurantResponse toRestaurantResponse(Restaurant restaurant) {

        return RestaurantResponse.builder()
                .id(restaurant.getId())
                .name(restaurant.getName())
                .description(restaurant.getDescription())
                .phoneNumber(restaurant.getPhoneNumber())
                .email(restaurant.getEmail())
                .address(restaurant.getAddress())
                .city(restaurant.getCity())
                .state(restaurant.getState())
                .zipCode(restaurant.getZipCode())
                .imageUrl(restaurant.getImageUrl())
                .rating(restaurant.getRating())
                .open(restaurant.getOpen())
                .build();
    }

    /**
     * Update an existing Restaurant Entity
     */
    public void updateRestaurant(Restaurant restaurant,
                                 RestaurantRequest request) {

        restaurant.setName(request.getName());
        restaurant.setDescription(request.getDescription());
        restaurant.setPhoneNumber(request.getPhoneNumber());
        restaurant.setEmail(request.getEmail());
        restaurant.setAddress(request.getAddress());
        restaurant.setCity(request.getCity());
        restaurant.setState(request.getState());
        restaurant.setZipCode(request.getZipCode());
        restaurant.setImageUrl(request.getImageUrl());
    }
}