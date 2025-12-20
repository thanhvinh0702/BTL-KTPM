package com.ecommerce.orderservice.client;
import com.ecommerce.orderservice.config.FeignConfig;
import com.ecommerce.orderservice.dto.CartResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "CartService", configuration = FeignConfig.class)
public interface CartClient {

    @GetMapping("/cart/{userId}")
    CartResponse getCartByUserId(@PathVariable("userId") String userId);

}

