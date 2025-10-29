package com.ecommerce.cartservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddProductRequest {
    private Long userId;
    private Long productId;
    private String productName;
    private Double price;
    private Integer quantity;
}
