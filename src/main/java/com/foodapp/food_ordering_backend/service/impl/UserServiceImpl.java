package com.foodapp.food_ordering_backend.service.impl;

import com.foodapp.food_ordering_backend.dto.request.RegisterRequest;
import com.foodapp.food_ordering_backend.entity.User;
import com.foodapp.food_ordering_backend.entity.enums.Role;
import com.foodapp.food_ordering_backend.repository.UserRepository;
import com.foodapp.food_ordering_backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {


    private final PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Override
    public String registerUser(RegisterRequest request) {

        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
               .password(passwordEncoder.encode(request.getPassword()))
                .phoneNumber(request.getPhoneNumber())
                .role(Role.USER)
                .enabled(true)
                .build();

        userRepository.save(user);

        return "User registered successfully";
    }
}
