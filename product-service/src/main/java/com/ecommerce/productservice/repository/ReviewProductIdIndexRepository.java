package com.ecommerce.productservice.repository;

import com.ecommerce.productservice.model.ReviewProductIdIndex;
import com.ecommerce.productservice.model.ReviewProductIdIndexKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewProductIdIndexRepository extends JpaRepository<ReviewProductIdIndex, ReviewProductIdIndexKey> {
    List<ReviewProductIdIndex> findByProductId(Long productId);
}
