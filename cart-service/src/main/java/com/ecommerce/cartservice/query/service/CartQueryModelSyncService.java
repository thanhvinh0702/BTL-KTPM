package com.ecommerce.cartservice.query.service;

import com.ecommerce.cartservice.command.model.CartItem;
import com.ecommerce.cartservice.dto.external.ProductResponse;
import com.ecommerce.cartservice.event.dto.EventType;
import com.ecommerce.cartservice.query.model.CartItemQuery;
import com.ecommerce.cartservice.query.model.CartQuery;
import com.ecommerce.cartservice.query.repository.CartQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
@Service
@RequiredArgsConstructor
public class CartQueryModelSyncService {

    private final CartQueryRepository cartQueryRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * Sync a single CartItem change event to query model
     */
    public void syncCartItem(String cartId, Long userId, CartItem cartItem, ProductResponse product, EventType type) {

        CartQuery cartQuery = cartQueryRepository.findByUserId(userId).orElseGet(() ->
                CartQuery.builder()
                        .cartId(cartId)
                        .userId(userId)
                        .items(new ArrayList<>())
                        .totalPrice(0.0)
                        .build()
        );

        List<CartItemQuery> items = cartQuery.getItems();

        switch (type) {
            case ADD:
            case UPDATE:
                // Check if item exists
                CartItemQuery existingItem = items.stream()
                        .filter(i -> i.getProductId().equals(cartItem.getProductId()))
                        .findFirst()
                        .orElse(null);

                String productName = product != null ? product.getName() :
                        (existingItem != null ? existingItem.getProductName() : null);
                String productImage = product != null ? product.getImageUrl() :
                        (existingItem != null ? existingItem.getProductImage() : null);
                Boolean isAvailable = product != null ? product.getIsAvailable() :
                        (existingItem != null ? existingItem.getIsAvailable() : true);

                CartItemQuery newItem = CartItemQuery.builder()
                        .productId(cartItem.getProductId())
                        .quantity(cartItem.getQuantity())
                        .priceAtAdd(cartItem.getPriceAtAdd())
                        .productName(productName)
                        .productImage(productImage)
                        .isAvailable(isAvailable)
                        .build();

                if (existingItem != null) {
                    items.remove(existingItem);
                }
                items.add(newItem);
                break;

            case DELETE:
                items.removeIf(i -> i.getProductId().equals(cartItem.getProductId()));
                break;

            case CLEAR:
                items.clear();
                break;
        }

        // Recalculate total price
        double totalPrice = items.stream()
                .mapToDouble(i -> i.getPriceAtAdd() * i.getQuantity())
                .sum();

        cartQuery.setTotalPrice(totalPrice);
        cartQuery.setItems(items);

        // Save to query DB
        cartQueryRepository.save(cartQuery);

        // Invalidate cache
        redisTemplate.delete("cart:" + userId);
    }
}
