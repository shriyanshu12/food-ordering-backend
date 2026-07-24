package com.foodapp.food_ordering_backend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name="Order_items")
public class OrderItem extends BaseEntity {
}
