package com.foodapp.food_ordering_backend.controller;

import com.foodapp.food_ordering_backend.dto.request.RestaurantRequest;
import com.foodapp.food_ordering_backend.dto.response.RestaurantResponse;
import com.foodapp.food_ordering_backend.service.RestaurantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/restaurants")
@RequiredArgsConstructor
@Tag(
        name = "Restaurants",
        description = "APIs for managing restaurants and searching restaurants"
)
public class RestaurantController {

    private final RestaurantService restaurantService;

    @Operation(
            summary = "Create Restaurant",
            description = "Creates a new restaurant (Admin only)"
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public RestaurantResponse createRestaurant(
            @Valid @RequestBody RestaurantRequest request) {

        return restaurantService.createRestaurant(request);
    }

//    @GetMapping
//    public List<RestaurantResponse> getAllRestaurants() {
//
//        return restaurantService.getAllRestaurants();
//    }

    @Operation(
            summary = "Get Restaurant",
            description = "Returns restaurant details by ID"
    )
    @GetMapping("/{id}")
    public RestaurantResponse getRestaurantById(
            @PathVariable Long id) {

        return restaurantService.getRestaurantById(id);
    }

    @Operation(
            summary = "Update Restaurant",
            description = "Updates restaurant details (Admin only)"
    )
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public RestaurantResponse updateRestaurant(
            @PathVariable Long id,
            @Valid @RequestBody RestaurantRequest request) {

        return restaurantService.updateRestaurant(id, request);
    }

    @Operation(
            summary = "Delete Restaurant",
            description = "Deletes a restaurant (Admin only)"
    )
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRestaurant(
            @PathVariable Long id) {

        restaurantService.deleteRestaurant(id);
    }

//    @GetMapping("/search")
//    public List<RestaurantResponse> searchRestaurants(
//            @RequestParam String keyword) {
//
//        return restaurantService.searchRestaurants(keyword);
//    }
//
//    @GetMapping("/city/{city}")
//    public List<RestaurantResponse> getRestaurantsByCity(
//            @PathVariable String city) {
//
//        return restaurantService.getRestaurantsByCity(city);
//    }
//
//    @GetMapping("/page")
//    public Page<RestaurantResponse> getRestaurants(
//
//            @RequestParam(defaultValue = "0") int page,
//
//            @RequestParam(defaultValue = "5") int size,
//
//            @RequestParam(defaultValue = "name") String sortBy,
//
//            @RequestParam(defaultValue = "asc") String direction) {
//
//        return restaurantService.getRestaurants(
//                page,
//                size,
//                sortBy,
//                direction
//        );
//    }


    @Operation(
            summary = "Search Restaurants",
            description = "Search restaurants using keyword, city, state, open status, minimum rating, pagination and sorting"
    )
    @GetMapping
    public Page<RestaurantResponse> getRestaurants(

            @RequestParam(required = false) String keyword,

            @RequestParam(required = false) String city,

            @RequestParam(required = false) String state,

            @RequestParam(required = false) Boolean open,

            @RequestParam(required = false) Double minRating,

            @RequestParam(defaultValue = "0") int page,

            @RequestParam(defaultValue = "5") int size,

            @RequestParam(defaultValue = "name") String sortBy,

            @RequestParam(defaultValue = "asc") String direction) {

        return restaurantService.getRestaurants(
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
    }
}