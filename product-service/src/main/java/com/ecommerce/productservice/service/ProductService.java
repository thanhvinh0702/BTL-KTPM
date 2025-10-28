package com.ecommerce.productservice.service;

import com.ecommerce.productservice.dto.ProductRequest;
import com.ecommerce.productservice.model.Product;
import com.ecommerce.productservice.repository.ProductRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ProductService {

    public final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Product findById(Long productId) {
        return productRepository.findById(productId).orElseThrow(() ->
                new NoSuchElementException("Product with id " + productId + " not found!"));
    }

    public List<Product> findProducts(String category, String name) {
        if (category != null && name != null) {
            return productRepository.findByCategoryNameAndName(category, name);
        } else if (category != null) {
            return productRepository.findByCategoryName(category);
        } else if (name != null) {
            return productRepository.findByName(name);
        } else {
            return productRepository.findAll();
        }
    }

    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    @PreAuthorize("@productSecurity.isOwner(#productId, #userId)")
    public Product updateProduct(Long productId, ProductRequest productRequest, Long userId) {
        Product existedProduct = productRepository.findById(productId)
                .orElseThrow(() -> new NoSuchElementException("Product with id " + productId + " not found!"));
        if (productRequest.getCategoryName() != null) {
            existedProduct.setCategoryName(productRequest.getCategoryName());
        }
        if (productRequest.getName() != null) {
            existedProduct.setName(productRequest.getName());
        }
        if (productRequest.getPrice() != null) {
            existedProduct.setPrice(productRequest.getPrice());
        }
        if (productRequest.getQuantity() != null) {
            existedProduct.setQuantity(productRequest.getQuantity());
        }
        if (productRequest.getImageUrl() != null) {
            existedProduct.setImageUrl(productRequest.getImageUrl());
        }
        if (productRequest.getDescription() != null) {
            existedProduct.setDescription(productRequest.getDescription());
        }
        return productRepository.save(existedProduct);
    }

    @PreAuthorize("@productSecurity.isOwner(#productId, #userId)")
    public Product deleteProduct(Long productId, Long userId) {
        Product existedProduct = productRepository.findById(productId)
                .orElseThrow(() -> new NoSuchElementException("Product with id " + productId + " not found!"));
        productRepository.deleteById(productId);
        return existedProduct;
    }
}
