package com.ecommerce.cartservice.mapper;

import com.ecommerce.cartservice.dto.response.CartItemResponse;
import com.ecommerce.cartservice.dto.response.CartResponse;
import com.ecommerce.cartservice.model.Cart;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CartMapper {
    public CartResponse toCartResponse(Cart cart) {
        List<CartItemResponse> itemResponses = cart.getItems().stream()
                .map(item -> CartItemResponse.builder()
                        .productId(item.getProductId())
                        .productName(item.getProductName())
                        .quantity(item.getQuantity())
                        .price(item.getPriceAtTime())
                        .totalPrice(item.getPriceAtTime() * item.getQuantity())
                        .build())
                .toList();

        return CartResponse.builder()
                .cartId(cart.getCartId())
                .userId(cart.getUserId())
                .items(itemResponses)
                .totalAmount(cart.getTotalAmount())
                .build();
    }
}
