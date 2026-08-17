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
import com.foodapp.food_ordering_backend.security.UserPrincipal;
import com.foodapp.food_ordering_backend.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;


    @Override
    public AuthResponse register(RegisterRequest request) {

        log.info("Registration request received for email: {}", request.getEmail());

        // Step 1: Check email
        if (userRepository.existsByEmail(request.getEmail())) {

            log.warn("Registration failed. Email already exists: {}",
                    request.getEmail());

            throw new BadRequestException("Email already exists");
        }

        // Step 2: Check phone
        if (userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            log.warn("Registration failed. Phone number already exists: {}",
                    request.getPhoneNumber());

            throw new BadRequestException("Phone number already exists");
        }

        // Step 3: Convert DTO -> Entity
        User user = userMapper.toUser(request);

        // Step 4: Encrypt password
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        log.debug("Password encoded for {}", request.getEmail());

        // Step 5: Save user
        user = userRepository.save(user);
        log.info("User created successfully with id: {}",
                user.getId());

        // Step 6: Convert Entity -> Response DTO
        UserResponse userResponse = userMapper.toUserResponse(user);

        String token = jwtUtil.generateToken(new UserPrincipal(user));
        log.debug("JWT generated for user: {}", user.getEmail());


        log.info("Registration completed successfully for {}",
                user.getEmail());
        // Step 7: Return response
        return AuthResponse.builder()
                .token(token)
                .user(userResponse)
                .build();
    }

    @Override
    public AuthResponse login(LoginRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                           request.getPassword()
                )
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        String token = jwtUtil.generateToken(new UserPrincipal(user));

        return AuthResponse.builder()
                .token(token)
                .user(userMapper.toUserResponse(user))
                .build();
    }
}