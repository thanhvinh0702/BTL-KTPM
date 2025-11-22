package com.ecommerce.cartservice.query.service;

import com.ecommerce.cartservice.exception.BadRequestException;
import com.ecommerce.cartservice.exception.ForbiddenException;
import com.ecommerce.cartservice.exception.NotFoundException;
import com.ecommerce.cartservice.exception.UnauthorizedException;
import com.ecommerce.cartservice.query.dto.response.CartQueryResponse;
import com.ecommerce.cartservice.query.mapper.CartQueryMapper;
import com.ecommerce.cartservice.query.model.CartQuery;
import com.ecommerce.cartservice.query.repository.CartQueryRepository;
import com.ecommerce.cartservice.security.RequireOwner;
import com.ecommerce.cartservice.security.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class CartQueryService {
    private final RedisTemplate<String, Object> redisTemplate;
    private final CartQueryRepository cartRepository;

    @RequireOwner
    public CartQueryResponse getCartByUserId(Long userId) {
        String key = "cart:" + userId;

        // CACHE HIT
        CartQuery cached = (CartQuery) redisTemplate.opsForValue().get(key);

        if (cached != null) {
            return CartQueryMapper.toResponse(cached);
        }

        // CACHE MISS
        CartQuery cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("Cart is not found"));

        double total = cart.getItems().stream()
                .mapToDouble(i -> i.getPriceAtAdd() * i.getQuantity())
                .sum();

        cart.setTotalPrice(total);

        redisTemplate.opsForValue().set(key, cart, Duration.ofMinutes(30));

        return CartQueryMapper.toResponse(cart);
    }
}
