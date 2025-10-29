package com.ecommerce.productservice.controller;

import com.ecommerce.productservice.dto.ProductRequest;
import com.ecommerce.productservice.model.Product;
import com.ecommerce.productservice.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<List<Product>> findAllProduct(@RequestParam(required = false) String category,
                                                        @RequestParam(required = false) String name) {
        return ResponseEntity.ok(productService.findProducts(category, name));
    }

    @GetMapping("/{productId}")
    public ResponseEntity<Product> findProductById(@PathVariable Long productId) {
        return ResponseEntity.ok(productService.findById(productId));
    }

    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody ProductRequest productRequest,
                                                 @RequestHeader("x-user-id") Long userId) {
        Product product = Product.builder()
                .name(productRequest.getName())
                .categoryName(productRequest.getCategoryName())
                .description(productRequest.getDescription())
                .imageUrl(productRequest.getImageUrl())
                .price(productRequest.getPrice())
                .quantity(productRequest.getQuantity())
                .ownerId(userId)
                .build();
        return new ResponseEntity<>(productService.createProduct(product), HttpStatus.CREATED);
    }

    @PutMapping("/{productId}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long productId,
                                                 @RequestBody ProductRequest productRequest,
                                                 @RequestHeader("x-user-id") Long userId) {
        return ResponseEntity.ok(productService.updateProduct(productId, productRequest, userId));
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Product> deleteProduct(@PathVariable Long productId,
                                                 @RequestHeader("x-user-id") Long userId) {
        return ResponseEntity.ok(productService.deleteProduct(productId, userId));
    }
}
