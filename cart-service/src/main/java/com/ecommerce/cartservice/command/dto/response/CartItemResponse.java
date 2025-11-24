package com.ecommerce.cartservice.command.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItemResponse {
    // Describe item in cart
    private Long productId;
    private Double price;
    private Integer quantity;
}

