package com.ecommerce.cartservice.service;

import com.ecommerce.cartservice.client.ProductClient;
import com.ecommerce.cartservice.dto.response.CartResponse;
import com.ecommerce.cartservice.model.command.Cart;
import com.ecommerce.cartservice.model.command.CartItem;
import com.ecommerce.cartservice.model.query.CartItemReadModel;
import com.ecommerce.cartservice.model.query.CartReadModel;
import com.ecommerce.cartservice.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartQueryService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final CartRepository cartRepository;
    private final ProductClient productClient;


    public CartReadModel getCartByUserId(Long userId) {
        String key = "cart:" + userId;

        // CACHE HIT
        CartReadModel cached = (CartReadModel) redisTemplate.opsForValue().get(key);

        if (cached != null) {
            return cached;
        }

        // CACHE MISS
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Not Found"));

        CartReadModel readModel = buildReadModel(cart);

        redisTemplate.opsForValue().set(key, readModel);

        return readModel;
    }

    private CartReadModel buildReadModel(Cart cart) {
        List<CartItemReadModel> items = new ArrayList<>();
        double totalPrice = 0;

        for (CartItem item : cart.getItems()) {
            var p = productClient.getProductById(item.getProductId());

            CartItemReadModel cartItemRead = new CartItemReadModel(
                    p.getId(),
                    item.getQuantity(),
                    p.getPrice(),
                    p.getName(),
                    p.getImageUrl()
            );

            items.add(cartItemRead);

            // Tính tổng
            totalPrice += p.getPrice() * item.getQuantity();
        }

        return new CartReadModel(
                cart.getCartId(),      // Thêm cartId
                cart.getUserId(),
                items,
                totalPrice             // Thêm totalPrice đã tính toán
        );
    }

}
