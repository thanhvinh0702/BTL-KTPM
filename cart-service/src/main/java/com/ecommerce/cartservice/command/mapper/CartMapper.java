package com.ecommerce.cartservice.command.mapper;

import com.ecommerce.cartservice.command.dto.response.CartItemResponse;
import com.ecommerce.cartservice.command.dto.response.CartResponse;
import com.ecommerce.cartservice.command.model.Cart;
import org.springframework.stereotype.Component;

import java.util.List;

public class CartMapper {

    public static CartResponse toResponse(Cart cart) {
        List<CartItemResponse> itemResponses = cart.getItems().stream()
                .map(item -> CartItemResponse.builder()
                        .productId(item.getProductId())
                        .productName(item.getProductName())
                        .quantity(item.getQuantity())
                        .price(item.getPriceAtAdd())
                        .build())
                .toList();

        return CartResponse.builder()
                .cartId(cart.getCartId())
                .userId(cart.getUserId())
                .items(itemResponses)
                .totalPrice(itemResponses.stream()
                        .mapToDouble(item -> item.getPrice() * item.getQuantity())
                        .sum())
                .createdAt(cart.getCreatedAt())
                .updatedAt(cart.getUpdatedAt())
                .build();
    }
}
