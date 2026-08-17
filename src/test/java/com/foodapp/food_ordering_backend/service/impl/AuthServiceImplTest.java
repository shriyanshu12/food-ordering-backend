        package com.foodapp.food_ordering_backend.service.impl;

import com.foodapp.food_ordering_backend.dto.request.LoginRequest;
import com.foodapp.food_ordering_backend.dto.request.RegisterRequest;
import com.foodapp.food_ordering_backend.dto.response.AuthResponse;
import com.foodapp.food_ordering_backend.dto.response.UserResponse;
import com.foodapp.food_ordering_backend.entity.User;
import com.foodapp.food_ordering_backend.exception.BadRequestException;
import com.foodapp.food_ordering_backend.exception.ResourceNotFoundException;
import com.foodapp.food_ordering_backend.mapper.UserMapper;
import com.foodapp.food_ordering_backend.repository.UserRepository;
import com.foodapp.food_ordering_backend.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthServiceImpl authService;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private User user;
    private UserResponse userResponse;

    @BeforeEach
    void setUp() {

        registerRequest = RegisterRequest.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .password("password123")
                .phoneNumber("9876543210")
                .build();

        loginRequest = LoginRequest.builder()
                .email("john@example.com")
                .password("password123")
                .build();

        user = User.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .password("encodedPassword")
                .phoneNumber("9876543210")
                .build();

        userResponse = UserResponse.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .build();
    }

    // ---------------------------------------------------------
    // REGISTER TESTS
    // ---------------------------------------------------------

    @Test
    void register_shouldRegisterSuccessfully() {

        when(userRepository.existsByEmail(registerRequest.getEmail()))
                .thenReturn(false);

        when(userRepository.existsByPhoneNumber(registerRequest.getPhoneNumber()))
                .thenReturn(false);

        when(userMapper.toUser(registerRequest))
                .thenReturn(user);

        when(passwordEncoder.encode("encodedPassword"))
                .thenReturn("encodedPassword");

        when(userRepository.save(user))
                .thenReturn(user);

        when(userMapper.toUserResponse(user))
                .thenReturn(userResponse);

        when(jwtUtil.generateToken(any()))
                .thenReturn("jwt-token");

        AuthResponse response = authService.register(registerRequest);

        assertNotNull(response);
        assertEquals("jwt-token", response.getToken());
        assertEquals(userResponse, response.getUser());

        verify(userRepository).existsByEmail(registerRequest.getEmail());
        verify(userRepository).existsByPhoneNumber(registerRequest.getPhoneNumber());
        verify(passwordEncoder).encode("encodedPassword");
        verify(userRepository).save(user);
        verify(jwtUtil).generateToken(any());
    }

    @Test
    void register_shouldThrowException_whenEmailAlreadyExists() {

        when(userRepository.existsByEmail(registerRequest.getEmail()))
                .thenReturn(true);

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> authService.register(registerRequest)
        );

        assertEquals("Email already exists", exception.getMessage());

        verify(userRepository)
                .existsByEmail(registerRequest.getEmail());

        verify(userRepository, never())
                .save(any(User.class));

        verify(passwordEncoder, never())
                .encode(anyString());
    }

    @Test
    void register_shouldThrowException_whenPhoneAlreadyExists() {

        when(userRepository.existsByEmail(registerRequest.getEmail()))
                .thenReturn(false);

        when(userRepository.existsByPhoneNumber(registerRequest.getPhoneNumber()))
                .thenReturn(true);

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> authService.register(registerRequest)
        );

        assertEquals(
                "Phone number already exists",
                exception.getMessage()
        );

        verify(userRepository)
                .existsByPhoneNumber(registerRequest.getPhoneNumber());

        verify(userRepository, never())
                .save(any(User.class));
    }

    @Test
    void register_shouldEncodePasswordBeforeSaving() {

        when(userRepository.existsByEmail(anyString()))
                .thenReturn(false);

        when(userRepository.existsByPhoneNumber(anyString()))
                .thenReturn(false);

        when(userMapper.toUser(registerRequest))
                .thenReturn(user);

        when(passwordEncoder.encode("encodedPassword"))
                .thenReturn("hashedPassword");

        when(userRepository.save(user))
                .thenReturn(user);

        when(userMapper.toUserResponse(user))
                .thenReturn(userResponse);

        when(jwtUtil.generateToken(any()))
                .thenReturn("jwt-token");

        authService.register(registerRequest);

        verify(passwordEncoder)
                .encode("encodedPassword");

        verify(userRepository)
                .save(user);

        assertEquals("hashedPassword", user.getPassword());
    }

    @Test
    void register_shouldGenerateJwtToken() {

        when(userRepository.existsByEmail(anyString()))
                .thenReturn(false);

        when(userRepository.existsByPhoneNumber(anyString()))
                .thenReturn(false);

        when(userMapper.toUser(registerRequest))
                .thenReturn(user);

        when(passwordEncoder.encode(anyString()))
                .thenReturn("encodedPassword");

        when(userRepository.save(user))
                .thenReturn(user);

        when(userMapper.toUserResponse(user))
                .thenReturn(userResponse);

        when(jwtUtil.generateToken(any()))
                .thenReturn("jwt-token");

        AuthResponse response =
                authService.register(registerRequest);

        assertEquals("jwt-token", response.getToken());

        verify(jwtUtil).generateToken(any());
    }

    // ---------------------------------------------------------
    // LOGIN TESTS
    // ---------------------------------------------------------

    @Test
    void login_shouldLoginSuccessfully() {

        when(userRepository.findByEmail(loginRequest.getEmail()))
                .thenReturn(Optional.of(user));

        when(jwtUtil.generateToken(any()))
                .thenReturn("jwt-token");

        when(userMapper.toUserResponse(user))
                .thenReturn(userResponse);

        AuthResponse response =
                authService.login(loginRequest);

        assertNotNull(response);
        assertEquals("jwt-token", response.getToken());
        assertEquals(userResponse, response.getUser());

        verify(authenticationManager)
                .authenticate(any(UsernamePasswordAuthenticationToken.class));

        verify(userRepository)
                .findByEmail(loginRequest.getEmail());

        verify(jwtUtil)
                .generateToken(any());
    }

    @Test
    void login_shouldThrowException_whenCredentialsAreInvalid() {

        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        assertThrows(
                BadCredentialsException.class,
                () -> authService.login(loginRequest)
        );

        verify(authenticationManager)
                .authenticate(any());

        verify(userRepository, never())
                .findByEmail(anyString());

        verify(jwtUtil, never())
                .generateToken(any());
    }

    @Test
    void login_shouldThrowException_whenUserNotFound() {

        when(userRepository.findByEmail(loginRequest.getEmail()))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> authService.login(loginRequest)
        );

        assertEquals("User not found", exception.getMessage());

        verify(authenticationManager)
                .authenticate(any());

        verify(userRepository)
                .findByEmail(loginRequest.getEmail());

        verify(jwtUtil, never())
                .generateToken(any());
    }

    @Test
    void login_shouldGenerateJwtToken() {

        when(userRepository.findByEmail(loginRequest.getEmail()))
                .thenReturn(Optional.of(user));

        when(jwtUtil.generateToken(any()))
                .thenReturn("jwt-token");

        when(userMapper.toUserResponse(user))
                .thenReturn(userResponse);

        AuthResponse response =
                authService.login(loginRequest);

        assertEquals("jwt-token", response.getToken());

        verify(jwtUtil)
                .generateToken(any());
    }
}
