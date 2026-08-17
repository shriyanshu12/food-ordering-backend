package com.foodapp.food_ordering_backend.service.impl;

import com.foodapp.food_ordering_backend.dto.request.RestaurantRequest;
import com.foodapp.food_ordering_backend.dto.response.RestaurantResponse;
import com.foodapp.food_ordering_backend.entity.Restaurant;
import com.foodapp.food_ordering_backend.exception.BadRequestException;
import com.foodapp.food_ordering_backend.exception.ResourceNotFoundException;
import com.foodapp.food_ordering_backend.mapper.RestaurantMapper;
import com.foodapp.food_ordering_backend.repository.RestaurantRepository;
import com.foodapp.food_ordering_backend.service.RestaurantService;
import com.foodapp.food_ordering_backend.specification.RestaurantSpecification;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RestaurantServiceImpl implements RestaurantService {

    private static final Logger log = LoggerFactory.getLogger(RestaurantServiceImpl.class);
    private final RestaurantRepository restaurantRepository;
    private final RestaurantMapper restaurantMapper;

    @Override
    public RestaurantResponse createRestaurant(RestaurantRequest request) {

        log.info("Creating restaurant: {}", request.getName());
        // Check duplicate email
        if (restaurantRepository.existsByEmail(request.getEmail())) {
            log.warn("Restaurant creation failed. Email already exists: {}",
                    request.getEmail());


            throw new BadRequestException("Restaurant email already exists");
        }

        // Check duplicate phone number
        if (restaurantRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            log.warn("Restaurant creation failed. Phone number already exists: {}",
                    request.getPhoneNumber());

            throw new BadRequestException("Restaurant phone number already exists");
        }

        Restaurant restaurant = restaurantMapper.toRestaurant(request);

        restaurant = restaurantRepository.save(restaurant);

        log.info(
                "Restaurant created successfully. ID={}, Name={}",
                restaurant.getId(),
                restaurant.getName()
        );

        return restaurantMapper.toRestaurantResponse(restaurant);
    }

    @Override
    public RestaurantResponse updateRestaurant(Long id,
                                               RestaurantRequest request) {

        log.info("Updating restaurant with ID={}", id);

        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> {

                    log.warn("Restaurant not found with ID={}", id);

                    return new ResourceNotFoundException("Restaurant not found");
                });

        // Check duplicate email
        if (!restaurant.getEmail().equals(request.getEmail())
                && restaurantRepository.existsByEmail(request.getEmail())) {
            log.warn(
                    "Restaurant update failed. Email already exists: {}",
                    request.getEmail()
            );

            throw new BadRequestException("Restaurant email already exists");
        }

        // Check duplicate phone
        if (!restaurant.getPhoneNumber().equals(request.getPhoneNumber())
                && restaurantRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            log.warn(
                    "Restaurant update failed. Phone number already exists: {}",
                    request.getPhoneNumber()
            );

            throw new BadRequestException("Restaurant phone number already exists");
        }

        restaurantMapper.updateRestaurant(restaurant, request);

        restaurant = restaurantRepository.save(restaurant);
        log.info(
                "Restaurant updated successfully. ID={}, Name={}",
                restaurant.getId(),
                restaurant.getName()
        );

        return restaurantMapper.toRestaurantResponse(restaurant);
    }

    @Override
    public void deleteRestaurant(Long id) {
        log.info("Deleting restaurant with ID={}", id);

        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Restaurant not found"));
        log.info(
                "Restaurant deleted successfully. ID={}, Name={}",
                restaurant.getId(),
                restaurant.getName()
        );
        restaurantRepository.delete(restaurant);
    }

    @Override
    public RestaurantResponse getRestaurantById(Long id) {

        log.info("Fetching restaurant with ID={}", id);
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() ->

                        new ResourceNotFoundException("Restaurant not found"));

        log.info(
                "Restaurant fetched successfully. ID={}, Name={}",
                restaurant.getId(),
                restaurant.getName()
        );

        return restaurantMapper.toRestaurantResponse(restaurant);
    }

    @Override
    public List<RestaurantResponse> getAllRestaurants() {
        log.info("Fetching all restaurants");

        List<Restaurant> restaurants = restaurantRepository.findAll();

        log.info("Fetched {} restaurants", restaurants.size());

        return restaurants.stream()
                .map(restaurantMapper::toRestaurantResponse)
                .toList();
    }


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
        log.info(
                "Searching restaurants. Keyword={}, City={}, State={}, Open={}, Rating>={}, Page={}, Size={}",
                keyword,
                city,
                state,
                open,
                minRating,
                page,
                size
        );

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

        Page<RestaurantResponse> response =restaurantRepository
                .findAll(specification, pageable)
                .map(restaurantMapper::toRestaurantResponse);

        log.info(
                "Restaurant search completed. {} restaurants found.",
                response.getTotalElements()
        );

        return response;
    }
}