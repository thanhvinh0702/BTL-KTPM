package com.ecommerce.orderservice.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.ecommerce.orderservice.model.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Integer> {

}
