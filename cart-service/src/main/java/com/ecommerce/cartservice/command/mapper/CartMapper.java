package com.ecommerce.cartservice.command.mapper;

import com.ecommerce.cartservice.command.dto.response.CartItemResponse;
import com.ecommerce.cartservice.command.dto.response.CartResponse;
import com.ecommerce.cartservice.command.model.Cart;
import org.springframework.stereotype.Component;

import java.util.List;

public class CartMapper {

    public static CartResponse toResponse(Cart cart) {
        return CartResponse.builder()
                .cartId(cart.getId())
                .userId(cart.getUserId())
                .createdAt(cart.getCreatedAt())
                .updatedAt(cart.getUpdatedAt())
                .build();
    }
}
