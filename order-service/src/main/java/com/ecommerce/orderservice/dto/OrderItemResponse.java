package com.ecommerce.orderservice.dto;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderItemResponse {
    private Long productId;
    private int quantity;
    private Double price;
}
