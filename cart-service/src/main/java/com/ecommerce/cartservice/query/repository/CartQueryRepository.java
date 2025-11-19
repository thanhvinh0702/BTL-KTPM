package com.ecommerce.cartservice.query.repository;

import com.ecommerce.cartservice.query.model.CartQuery;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartQueryRepository extends MongoRepository<CartQuery, String> {

    Optional<CartQuery> findByUserId(Long userId);

}
