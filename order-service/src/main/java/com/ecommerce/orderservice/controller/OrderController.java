package com.ecommerce.orderservice.controller;

import com.ecommerce.orderservice.model.Orders;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.orderservice.service.OrdersService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/ecom/orders")
public class OrderController {

    private final OrdersService ordersService;

    @PostMapping("/placed")
    public ResponseEntity<Orders> placeOrder() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Orders placeOrder = ordersService.placeOrder(authentication.getName());
        return ResponseEntity.ok(placeOrder);
    }

}

