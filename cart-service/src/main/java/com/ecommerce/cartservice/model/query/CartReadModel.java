package com.ecommerce.cartservice.model.query;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartReadModel {
    private String cartId;
    private Long userId;
    private List<CartItemReadModel> items;

    private Double totalPrice;
}
