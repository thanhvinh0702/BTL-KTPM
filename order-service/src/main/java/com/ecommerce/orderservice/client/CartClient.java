package com.ecommerce.orderservice.client;
import com.ecommerce.orderservice.dto.CartResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Feign Client giao tiếp với CartService
 */
@FeignClient(name = "CartService")
public interface CartClient {

    @DeleteMapping("/empty-cart/{cartId}")
    ResponseEntity<Void> emptyCart(@PathVariable("cartID") Long cartId);

    /**
     * Waiting the service into cart service
     * @param cartId
     * @return
     */
    @GetMapping("/products/{userId}")
    public CartResponse getCartByUserId(@PathVariable("cartID") Long cartId);

}

