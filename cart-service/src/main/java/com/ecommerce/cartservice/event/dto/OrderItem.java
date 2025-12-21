package com.ecommerce.cartservice.event.dto;

import lombok.Data;

@Data
public class OrderItem {
    private Integer quantity;
    private Double price;
    private Long productId;
}
