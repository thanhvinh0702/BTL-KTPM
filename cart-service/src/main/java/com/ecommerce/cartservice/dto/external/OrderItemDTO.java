package com.ecommerce.cartservice.dto.external;

public class OrderItemDTO {
    /**
     * Dùng để gửi danh sách OrderItemDTO cho order-service
     */
    private Long productId;
    private int quantity;
    private double price;
}
