package com.ecommerce.cartservice.query.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartQueryResponse {
    private String cartId;
    private Long userId;
    private Double totalPrice;
    private List<CartItemQueryResponse> items;
}

