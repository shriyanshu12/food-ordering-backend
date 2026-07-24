package com.foodapp.food_ordering_backend.mapper;

import com.foodapp.food_ordering_backend.dto.request.RegisterRequest;
import com.foodapp.food_ordering_backend.dto.response.UserResponse;
import com.foodapp.food_ordering_backend.entity.User;
import com.foodapp.food_ordering_backend.entity.enums.Role;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {


     // --------------> Convert RegisterRequest -> User Entity

    public User toUser(RegisterRequest request) {

        return User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(request.getPassword()) // Password will be encoded in AuthService
                .phoneNumber(request.getPhoneNumber())
                .role(Role.USER)
                .enabled(true)
                .build();
    }

     // ---------------------> Convert User Entity -> UserResponse

    public UserResponse toUserResponse(User user) {

        return UserResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .role(user.getRole())
                .enabled(user.getEnabled())
                .build();
    }

}