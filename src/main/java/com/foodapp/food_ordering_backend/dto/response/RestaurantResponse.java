package com.foodapp.food_ordering_backend.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RestaurantResponse {

    private Long id;

    private String name;

    private String description;

    private String phoneNumber;

    private String email;

    private String address;

    private String city;

    private String state;

    private String zipCode;

    private String imageUrl;

    private Double rating;

    private Boolean open;
}