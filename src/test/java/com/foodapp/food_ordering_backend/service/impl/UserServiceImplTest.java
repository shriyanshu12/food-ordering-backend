        package com.foodapp.food_ordering_backend.service.impl;

import com.foodapp.food_ordering_backend.dto.request.RegisterRequest;
import com.foodapp.food_ordering_backend.entity.User;
import com.foodapp.food_ordering_backend.entity.enums.Role;
import com.foodapp.food_ordering_backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private RegisterRequest registerRequest;

    @BeforeEach
    void setUp() {

        registerRequest = RegisterRequest.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .password("password123")
                .phoneNumber("9876543210")
                .build();
    }

    @Test
    void registerUser_shouldRegisterSuccessfully() {

        when(passwordEncoder.encode("password123"))
                .thenReturn("encodedPassword");

        userService.registerUser(registerRequest);

        verify(passwordEncoder)
                .encode("password123");

        verify(userRepository)
                .save(any(User.class));
    }

    @Test
    void registerUser_shouldEncodePassword() {

        when(passwordEncoder.encode("password123"))
                .thenReturn("encodedPassword");

        userService.registerUser(registerRequest);

        verify(passwordEncoder)
                .encode("password123");
    }

    @Test
    void registerUser_shouldSaveUserWithCorrectDetails() {

        when(passwordEncoder.encode("password123"))
                .thenReturn("encodedPassword");

        userService.registerUser(registerRequest);

        verify(userRepository).save(argThat(user ->
                user.getFirstName().equals("John")
                        && user.getLastName().equals("Doe")
                        && user.getEmail().equals("john@example.com")
                        && user.getPhoneNumber().equals("9876543210")
                        && user.getPassword().equals("encodedPassword")
        ));
    }

    @Test
    void registerUser_shouldAssignUserRole() {

        when(passwordEncoder.encode("password123"))
                .thenReturn("encodedPassword");

        userService.registerUser(registerRequest);

        verify(userRepository).save(argThat(user ->
                user.getRole() == Role.USER
        ));
    }

    @Test
    void registerUser_shouldEnableUser() {

        when(passwordEncoder.encode("password123"))
                .thenReturn("encodedPassword");

        userService.registerUser(registerRequest);

        verify(userRepository).save(argThat(user ->
                Boolean.TRUE.equals(user.getEnabled())
        ));
    }

    @Test
    void registerUser_shouldReturnSuccessMessage() {

        when(passwordEncoder.encode("password123"))
                .thenReturn("encodedPassword");

        String result =
                userService.registerUser(registerRequest);

        assertEquals(
                "User registered successfully",
                result
        );

        verify(userRepository)
                .save(any(User.class));
    }
}
