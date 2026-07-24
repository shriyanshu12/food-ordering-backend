package com.foodapp.food_ordering_backend.service;

import com.foodapp.food_ordering_backend.dto.request.LoginRequest;
import com.foodapp.food_ordering_backend.dto.request.RegisterRequest;
import com.foodapp.food_ordering_backend.dto.response.AuthResponse;

public interface AuthService {

    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest loginRequest);
}
