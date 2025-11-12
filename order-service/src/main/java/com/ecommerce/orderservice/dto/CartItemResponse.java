package com.ecommerce.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItemResponse {
    /**
     * mô tả chi tiết từng item trong giỏ hàng
     */
    private Long productId;
    private String productName;
    private String imageUrl;
    private Double price;
    private Integer quantity;
    private Double totalPrice; // price * quantity
}
