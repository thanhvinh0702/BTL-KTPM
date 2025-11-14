package com.ecommerce.cartservice.model.query;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItemReadModel {
    private Long userId;
    private Integer quantity;
    private Double priceAtAdd;
    private String productName;
    private String productImage;
}
