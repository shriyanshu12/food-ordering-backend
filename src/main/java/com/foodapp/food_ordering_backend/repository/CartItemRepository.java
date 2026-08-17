package com.foodapp.food_ordering_backend.repository;

import com.foodapp.food_ordering_backend.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    List<CartItem> findByCart_Id(Long cartId);

    Optional<CartItem> findByCart_IdAndMenuItem_Id(
            Long cartId,
            Long menuItemId
    );

    void deleteByCart_Id(Long cartId);
}