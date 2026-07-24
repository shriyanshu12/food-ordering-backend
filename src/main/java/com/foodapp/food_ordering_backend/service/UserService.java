package com.foodapp.food_ordering_backend.service;

import com.foodapp.food_ordering_backend.dto.request.RegisterRequest;

public interface UserService {
    String registerUser(RegisterRequest request);
}