package com.ecommerce.cartservice.client;

import com.ecommerce.cartservice.dto.external.ProductResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Feign Client giao tiếp với ProductService
 */
@FeignClient(name = "ProductService")
public interface ProductClient {

    @GetMapping("api/v1/products/{productId}")
    ProductResponse getProductById(@PathVariable("productId") Long productId);
}
