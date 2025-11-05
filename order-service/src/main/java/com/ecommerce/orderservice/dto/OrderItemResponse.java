package com.ecommerce.orderservice.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class OrderItemResponse {
    private int productId;
    private int quantity;
    private BigDecimal price;
}
