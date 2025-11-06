package com.ecommerce.orderservice.security;

import com.ecommerce.orderservice.repository.OrderRepository;
import org.springframework.stereotype.Component;

@Component("orderSecurity")
public class OrderSecurity {

    private final OrderRepository orderRepository;

    public OrderSecurity(OrderRepository productRepository) {
        this.orderRepository = productRepository;
    }
}

