package com.ecommerce.cartservice.command.repository;

import com.ecommerce.cartservice.command.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemCommandRepository extends JpaRepository<CartItem, Long> {

    CartItem findByCartIdAndProductId(String cartId, Long productId);
    void deleteAllByCartId(String cartId);
}
