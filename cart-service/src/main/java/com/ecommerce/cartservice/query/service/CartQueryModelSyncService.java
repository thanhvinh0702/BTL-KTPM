package com.ecommerce.cartservice.query.service;

import com.ecommerce.cartservice.command.model.Cart;
import com.ecommerce.cartservice.query.model.CartItemQuery;
import com.ecommerce.cartservice.query.model.CartQuery;
import com.ecommerce.cartservice.query.repository.CartQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CartQueryModelSyncService {

    private final CartQueryRepository cartQueryRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    public void sync(Cart cart) {
        CartQuery cartQuery = CartQuery.builder()
                .cartId(cart.getCartId())
                .userId(cart.getUserId())
                .totalPrice(cart.getItems().stream()
                        .mapToDouble(i -> i.getPriceAtAdd() * i.getQuantity())
                        .sum())
                .items(
                        cart.getItems().stream()
                                .map(i -> CartItemQuery.builder()
                                        .productId(i.getProductId())
                                        .productName(i.getProductName())
                                        .productImage(i.getProductImage())
                                        .priceAtAdd(i.getPriceAtAdd())
                                        .quantity(i.getQuantity())
                                        .build())
                                .toList()
                )
                .build();

        cartQueryRepository.save(cartQuery);

        // invalidate cache
        redisTemplate.delete("cart:" + cart.getUserId());

    }
}
