package com.ecommerce.orderservice.controller;

import com.ecommerce.orderservice.dto.OrderResponse;
import com.ecommerce.orderservice.model.Orders;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.ecommerce.orderservice.service.OrdersService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/ecom/orders")
public class OrderController {

    private final OrdersService ordersService;

    @PostMapping("/placed/{userId}")
    public ResponseEntity<Orders> placeOrder(@PathVariable String userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Orders placeOrder = ordersService.placeOrder(authentication.getName());
        return ResponseEntity.ok(placeOrder);
    }

    /**
     * GET METHOD
     */
    @GetMapping("/all")
    public ResponseEntity<List<OrderResponse>> getAllOrders() {
        return ResponseEntity.ok(ordersService.getAllOrder());
    }

    @GetMapping("/orders/{userId}")
    public ResponseEntity<List<OrderResponse>> getAllByUser(@PathVariable String userId) {
        return ResponseEntity.ok(ordersService.getAllOrderByUserId(userId));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable Long orderId) {
        return ResponseEntity.ok(ordersService.getOrderById(orderId));
    }

    @GetMapping("/date/{date}")
    public ResponseEntity<List<OrderResponse>> getAllByDate(@PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime date) {
        return ResponseEntity.ok(ordersService.getAllOrderByDate(date));
    }

    /**
     * DELETE METHOD
     */
    @DeleteMapping("/users/{userId}/{orderId}")
    public ResponseEntity<String> deleteOrder(@PathVariable String userId,
                            @PathVariable Long orderId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!userId.equals(authentication.getName())) {
            throw new AccessDeniedException("Insufficient permission");
        }
        ordersService.deleteOrder(authentication.getName(), orderId);
        return ResponseEntity.ok("delete order successfully");
    }
}

