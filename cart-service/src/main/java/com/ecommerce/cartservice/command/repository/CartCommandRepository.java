package com.ecommerce.cartservice.command.repository;

import com.ecommerce.cartservice.command.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartCommandRepository extends JpaRepository<Cart, String> {
    Optional<Cart> findByUserId(Long userId);
}
