package com.ecommerce.cartservice.command.repository;

import com.ecommerce.cartservice.command.model.Cart;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartCommandRepository extends MongoRepository<Cart, String> {
    Optional<Cart> findByUserId(Long userId);
}
