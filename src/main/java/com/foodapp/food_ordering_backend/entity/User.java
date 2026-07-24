package com.foodapp.food_ordering_backend.entity;

import com.foodapp.food_ordering_backend.entity.enums.Role;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseEntity {

    @Column(nullable = false, length = 50)
    private String firstName;

    @Column(nullable = false, length = 50)
    private String lastName;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(name = "phone_number", nullable = false, unique = true, length = 15)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Builder.Default
    @Column(nullable = false)
    private Boolean enabled = true;

//    @OneToOne(mappedBy = "user",
//            cascade = CascadeType.ALL,
//            fetch = FetchType.LAZY)
//    private Cart cart;
//
//    @OneToMany(mappedBy = "user",
//            cascade = CascadeType.ALL,
//            fetch = FetchType.LAZY)
//    @Builder.Default
//    private List<Order> orders = new ArrayList<>();
}