package com.ecommerce.cartservice.query.service;

import com.ecommerce.cartservice.query.dto.response.CartQueryResponse;
import com.ecommerce.cartservice.query.mapper.CartQueryMapper;
import com.ecommerce.cartservice.query.model.CartQuery;
import com.ecommerce.cartservice.query.repository.CartQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class CartQueryService {
    private final RedisTemplate<String, Object> redisTemplate;
    private final CartQueryRepository cartRepository;

    public CartQueryResponse getCartByUserId(Long userId) {
        String key = "cart:" + userId;

        // CACHE HIT
        CartQuery cached = (CartQuery) redisTemplate.opsForValue().get(key);

        if (cached != null) {
            return CartQueryMapper.toResponse(cached);
        }

        // CACHE MISS
        CartQuery cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Not Found"));

        double total = cart.getItems().stream()
                .mapToDouble(i -> i.getPriceAtAdd() * i.getQuantity())
                .sum();

        cart.setTotalPrice(total);

        redisTemplate.opsForValue().set(key, cart, Duration.ofMinutes(30));

        return CartQueryMapper.toResponse(cart);
    }
}
