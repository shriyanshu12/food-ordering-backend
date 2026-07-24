package com.foodapp.food_ordering_backend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name="orders")
public class Order extends BaseEntity{
}
