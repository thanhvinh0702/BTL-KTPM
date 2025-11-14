package com.ecommerce.cartservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test/redis")
@RequiredArgsConstructor
public class TestRedisController {
    private final RedisTemplate<String, Object> redisTemplate;

    @GetMapping
    public Object test() {
        String key = "test_key";
        String value = "Redis OK!";

        redisTemplate.opsForValue().set(key, value);
        Object result = redisTemplate.opsForValue().get(key);

        return result != null ? result : "Redis FAILED";
    }
}
