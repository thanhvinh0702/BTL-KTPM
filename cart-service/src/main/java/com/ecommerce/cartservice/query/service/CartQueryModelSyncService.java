package com.ecommerce.cartservice.query.service;

import com.ecommerce.cartservice.command.model.Cart;
import com.ecommerce.cartservice.dto.external.ProductResponse;
import com.ecommerce.cartservice.query.model.CartItemQuery;
import com.ecommerce.cartservice.query.model.CartQuery;
import com.ecommerce.cartservice.query.repository.CartQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CartQueryModelSyncService {

    private final CartQueryRepository cartQueryRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    public void sync(Cart cart) {
        sync(cart, null);
    }

    public void sync(Cart cart, ProductResponse product) {
        CartQuery existingQuery = cartQueryRepository.findByUserId(cart.getUserId())
                .orElse(null);

        List<CartItemQuery> items = cart.getItems().stream()
                .map(commandItem -> {

                    CartItemQuery oldItem = existingQuery != null
                            ? existingQuery.getItems().stream()
                            .filter(q -> q.getProductId().equals(commandItem.getProductId()))
                            .findFirst().orElse(null)
                            : null;

                    boolean isUpdatedProduct =
                            product != null && product.getId().equals(commandItem.getProductId());

                    String productName = isUpdatedProduct && product != null
                            ? product.getName()
                            : oldItem != null ? oldItem.getProductName() : null;

                    String productImage = isUpdatedProduct && product != null
                            ? product.getImageUrl()
                            : oldItem != null ? oldItem.getProductImage() : null;

                    Boolean isAvailable = isUpdatedProduct && product != null
                            ? product.getAvailable()
                            : oldItem != null ? oldItem.getIsAvailable() : true;

                    return CartItemQuery.builder()
                            .productId(commandItem.getProductId())
                            .quantity(commandItem.getQuantity())
                            .priceAtAdd(commandItem.getPriceAtAdd())
                            .productName(productName)
                            .productImage(productImage)
                            .isAvailable(isAvailable)
                            .build();
                })
                .toList();

        double totalPrice = items.stream()
                .mapToDouble(i -> i.getPriceAtAdd() * i.getQuantity())
                .sum();

        CartQuery newQuery = CartQuery.builder()
                .cartId(cart.getCartId())          // FIX 1: Thêm cartId
                .userId(cart.getUserId())
                .totalPrice(totalPrice)            // FIX 2: Thêm totalPrice
                .items(items)
                .build();

        // Save to Mongo
        cartQueryRepository.save(newQuery);

        // invalidate cache
        redisTemplate.delete("cart:" + cart.getUserId());
    }
}
