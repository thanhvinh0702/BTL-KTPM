package com.ecommerce.cartservice.query.mapper;

import com.ecommerce.cartservice.query.dto.response.CartItemQueryResponse;
import com.ecommerce.cartservice.query.dto.response.CartQueryResponse;
import com.ecommerce.cartservice.query.model.CartItemQuery;
import com.ecommerce.cartservice.query.model.CartQuery;
import org.springframework.stereotype.Component;

@Component
public class CartQueryMapper {
    public static CartQueryResponse toResponse(CartQuery cart) {
        return CartQueryResponse.builder()
                .cartId(cart.getCartId())
                .userId(cart.getUserId())
                .totalPrice(cart.getTotalPrice())
                .items(cart.getItems().stream()
                        .map(CartQueryMapper::toItemResponse)
                        .toList())
                .build();
    }

    private static CartItemQueryResponse toItemResponse(CartItemQuery item) {
        return CartItemQueryResponse.builder()
                .productId(item.getProductId())
                .productName(item.getProductName())
                .productImage(item.getProductImage())
                .priceAtAdd(item.getPriceAtAdd())
                .quantity(item.getQuantity())
                .isAvailable(item.getIsAvailable())
                .build();
    }
}
