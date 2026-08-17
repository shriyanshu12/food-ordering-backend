package com.foodapp.food_ordering_backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.foodapp.food_ordering_backend.dto.request.LoginRequest;
import com.foodapp.food_ordering_backend.dto.request.RegisterRequest;
import com.foodapp.food_ordering_backend.dto.response.AuthResponse;
import com.foodapp.food_ordering_backend.dto.response.UserResponse;
import com.foodapp.food_ordering_backend.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @Mock
    private AuthService authService;

    private AuthController authController;

    @BeforeEach
    void setUp() {

        authController = new AuthController(authService);

        mockMvc = MockMvcBuilders
                .standaloneSetup(authController)
                .build();

        objectMapper = new ObjectMapper();
    }

    @Test
    void register_shouldReturnCreated() throws Exception {

        RegisterRequest request = RegisterRequest.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .password("Password@123")
                .phoneNumber("9876543210")
                .build();

        UserResponse userResponse = UserResponse.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .phoneNumber("9876543210")
                .build();

        AuthResponse response = AuthResponse.builder()
                .token("test-jwt-token")
                .user(userResponse)
                .build();

        when(authService.register(any(RegisterRequest.class)))
                .thenReturn(response);

        mockMvc.perform(
                        post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token")
                        .value("test-jwt-token"))
                .andExpect(jsonPath("$.user.email")
                        .value("john@example.com"));

        verify(authService, times(1))
                .register(any(RegisterRequest.class));
    }

    @Test
    void login_shouldReturnOk() throws Exception {

        LoginRequest request = LoginRequest.builder()
                .email("john@example.com")
                .password("Password@123")
                .build();

        UserResponse userResponse = UserResponse.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .phoneNumber("9876543210")
                .build();

        AuthResponse response = AuthResponse.builder()
                .token("test-jwt-token")
                .user(userResponse)
                .build();

        when(authService.login(any(LoginRequest.class)))
                .thenReturn(response);

        mockMvc.perform(
                        post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token")
                        .value("test-jwt-token"))
                .andExpect(jsonPath("$.user.email")
                        .value("john@example.com"));

        verify(authService, times(1))
                .login(any(LoginRequest.class));
    }

    @Test
    void register_shouldCallAuthService() throws Exception {

        RegisterRequest request = RegisterRequest.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .password("Password@123")
                .phoneNumber("9876543210")
                .build();

        AuthResponse response = AuthResponse.builder()
                .token("test-jwt-token")
                .build();

        when(authService.register(any(RegisterRequest.class)))
                .thenReturn(response);

        mockMvc.perform(
                        post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isCreated());

        verify(authService, times(1))
                .register(any(RegisterRequest.class));
    }

    @Test
    void login_shouldCallAuthService() throws Exception {

        LoginRequest request = LoginRequest.builder()
                .email("john@example.com")
                .password("Password@123")
                .build();

        AuthResponse response = AuthResponse.builder()
                .token("test-jwt-token")
                .build();

        when(authService.login(any(LoginRequest.class)))
                .thenReturn(response);

        mockMvc.perform(
                        post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk());

        verify(authService, times(1))
                .login(any(LoginRequest.class));
    }

}
