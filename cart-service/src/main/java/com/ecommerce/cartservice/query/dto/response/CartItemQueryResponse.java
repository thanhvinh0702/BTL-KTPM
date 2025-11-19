package com.ecommerce.cartservice.query.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItemQueryResponse {
    private Long productId;
    private String productName;
    private String productImage;
    private Double priceAtAdd;
    private Integer quantity;
}
