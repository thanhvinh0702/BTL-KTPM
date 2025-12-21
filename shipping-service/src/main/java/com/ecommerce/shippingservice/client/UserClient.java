package com.ecommerce.shippingservice.client;

import com.ecommerce.shippingservice.config.FeignConfig;
import com.ecommerce.shippingservice.dto.client.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "UserService", configuration = FeignConfig.class)
public interface UserClient {

    @GetMapping("/api/v1/users/{userId}")
    UserResponse findById(@PathVariable Long userId,
                          @RequestHeader("X-USER-ID") String headerUserId,
                          @RequestHeader("X-USER-ROLE") String userRole);
}
