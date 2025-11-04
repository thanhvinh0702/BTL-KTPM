package com.ecommerce.cartservice.repository;

import com.ecommerce.cartservice.model.Cart;
import com.ecommerce.cartservice.model.CartItem;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository

public interface CartRepository extends JpaRepository<Cart, Long> {

    // Tìm cart theo userId (vì mỗi user có 1 cart)
    Optional<Cart> findByUserId(Long userId);
}
