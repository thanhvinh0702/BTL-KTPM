package com.ecommerce.productservice.security;

import com.ecommerce.productservice.repository.ProductRepository;
import org.springframework.stereotype.Component;

@Component("productSecurity")
public class ProductSecurity {

    private final ProductRepository productRepository;

    public ProductSecurity(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public boolean isOwner(Long productId, Long userId) {
        return productRepository.findById(productId)
                .map(product -> product.getOwnerId().equals(userId))
                .orElse(false);
    }
}
