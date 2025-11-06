package com.ecommerce.paymentservice.client;

import com.ecommerce.paymentservice.dto.external.OrderResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "OrderService")
public interface OrderClient {

    @GetMapping("/orders/{orderId}")
    OrderResponse getOrderById(@PathVariable Long userId);

    @PutMapping("/orders/{orderId}/status") // hiện tại chưa có trong order-service
    void updateOrderStatus(@PathVariable Long orderId, @RequestParam String status);

}
