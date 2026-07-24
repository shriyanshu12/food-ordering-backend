package com.foodapp.food_ordering_backend.service.impl;

import com.foodapp.food_ordering_backend.dto.request.RestaurantRequest;
import com.foodapp.food_ordering_backend.dto.response.RestaurantResponse;
import com.foodapp.food_ordering_backend.entity.Restaurant;
import com.foodapp.food_ordering_backend.exception.BadRequestException;
import com.foodapp.food_ordering_backend.exception.ResourceNotFoundException;
import com.foodapp.food_ordering_backend.mapper.RestaurantMapper;
import com.foodapp.food_ordering_backend.repository.RestaurantRepository;
import com.foodapp.food_ordering_backend.service.RestaurantService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Sort;
import com.foodapp.food_ordering_backend.specification.RestaurantSpecification;
import org.springframework.data.jpa.domain.Specification;
import com.foodapp.food_ordering_backend.specification.RestaurantSpecification;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RestaurantServiceImpl implements RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final RestaurantMapper restaurantMapper;

    @Override
    public RestaurantResponse createRestaurant(RestaurantRequest request) {

        // Check duplicate email
        if (restaurantRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Restaurant email already exists");
        }

        // Check duplicate phone number
        if (restaurantRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new BadRequestException("Restaurant phone number already exists");
        }

        Restaurant restaurant = restaurantMapper.toRestaurant(request);

        restaurant = restaurantRepository.save(restaurant);

        return restaurantMapper.toRestaurantResponse(restaurant);
    }

    @Override
    public RestaurantResponse updateRestaurant(Long id,
                                               RestaurantRequest request) {

        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Restaurant not found"));

        // Check duplicate email
        if (!restaurant.getEmail().equals(request.getEmail())
                && restaurantRepository.existsByEmail(request.getEmail())) {

            throw new BadRequestException("Restaurant email already exists");
        }

        // Check duplicate phone
        if (!restaurant.getPhoneNumber().equals(request.getPhoneNumber())
                && restaurantRepository.existsByPhoneNumber(request.getPhoneNumber())) {

            throw new BadRequestException("Restaurant phone number already exists");
        }

        restaurantMapper.updateRestaurant(restaurant, request);

        restaurant = restaurantRepository.save(restaurant);

        return restaurantMapper.toRestaurantResponse(restaurant);
    }

    @Override
    public void deleteRestaurant(Long id) {

        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Restaurant not found"));

        restaurantRepository.delete(restaurant);
    }

    @Override
    public RestaurantResponse getRestaurantById(Long id) {

        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Restaurant not found"));

        return restaurantMapper.toRestaurantResponse(restaurant);
    }

    @Override
    public List<RestaurantResponse> getAllRestaurants() {

        return restaurantRepository.findAll()
                .stream()
                .map(restaurantMapper::toRestaurantResponse)
                .toList();
    }

//    @Override
//    public List<RestaurantResponse> searchRestaurants(String keyword) {
//
//        List<Restaurant> restaurants =
//                restaurantRepository.findByNameContainingIgnoreCase(keyword);
//
//        if (restaurants.isEmpty()) {
//            throw new ResourceNotFoundException(
//                    "No restaurants found with keyword: " + keyword
//            );
//        }
//
//        return restaurants.stream()
//                .map(restaurantMapper::toRestaurantResponse)
//                .toList();
//    }
//
//    @Override
//    public List<RestaurantResponse> getRestaurantsByCity(String city) {
//
//        List<Restaurant> restaurants =
//                restaurantRepository.findByCityIgnoreCase(city);
//
//        if (restaurants.isEmpty()) {
//            throw new ResourceNotFoundException(
//                    "No restaurants found in city: " + city
//            );
//        }
//
//        return restaurants.stream()
//                .map(restaurantMapper::toRestaurantResponse)
//                .toList();
//    }
//
//
//
//    @Override
//    public Page<RestaurantResponse> getRestaurants(
//            int page,
//            int size,
//            String sortBy,
//            String direction) {
//
//        Sort sort = direction.equalsIgnoreCase("desc")
//                ? Sort.by(sortBy).descending()
//                : Sort.by(sortBy).ascending();
//
//        Pageable pageable = PageRequest.of(page, size, sort);
//
//        return restaurantRepository.findAll(pageable)
//                .map(restaurantMapper::toRestaurantResponse);
//    }


    @Override
    public Page<RestaurantResponse> getRestaurants(
            String keyword,
            String city,
            String state,
            Boolean open,
            Double minRating,
            int page,
            int size,
            String sortBy,
            String direction) {

        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Specification<Restaurant> specification =
                RestaurantSpecification.filterRestaurants(
                        keyword,
                        city,
                        state,
                        open,
                        minRating
                );

        return restaurantRepository
                .findAll(specification, pageable)
                .map(restaurantMapper::toRestaurantResponse);
    }
}