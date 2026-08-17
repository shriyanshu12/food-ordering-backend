package com.foodapp.food_ordering_backend.controller;

import com.foodapp.food_ordering_backend.dto.request.LoginRequest;
import com.foodapp.food_ordering_backend.dto.request.RegisterRequest;
import com.foodapp.food_ordering_backend.dto.response.AuthResponse;
import com.foodapp.food_ordering_backend.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(
        name = "Authentication",
        description = "APIs for user registration and login"
)
public class AuthController {

    private final AuthService authService;

    //Register User
    @Operation(
            summary = "Register User",
            description = "Registers a new user and returns a JWT token"
    )
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @Valid @RequestBody RegisterRequest request) {

        AuthResponse response = authService.register(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }


     //Login User

    @Operation(
            summary = "Login User",
            description = "Authenticates the user and returns a JWT token"
    )
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request) {

        AuthResponse response = authService.login(request);

        return ResponseEntity.ok(response);
    }
}