package com.foodapp.food_ordering_backend.dto.response;

import com.foodapp.food_ordering_backend.entity.enums.Role;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {

    private Long id;

    private String firstName;

    private String lastName;

    private String email;

    private String phoneNumber;

    private Role role;

    private Boolean enabled;
}