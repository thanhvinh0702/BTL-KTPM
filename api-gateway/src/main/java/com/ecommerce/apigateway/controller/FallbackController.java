package com.ecommerce.apigateway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @GetMapping("/product")
    public Mono<ResponseEntity<Map<String, String>>> productFallback() {
        Map<String, String> response = new HashMap<>();
        response.put("code", "503");
        response.put("message", "Hệ thống sản phẩm đang quá tải, vui lòng thử lại sau.");
        response.put("type", "BULKHEAD_LIMIT_EXCEEDED");

        return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response));
    }

    @GetMapping("/order")
    public Mono<ResponseEntity<Map<String, String>>> orderFallback() {
        Map<String, String> response = new HashMap<>();
        response.put("code", "503");
        response.put("message", "Hệ thống đặt hàng đang bận.");
        return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response));
    }
}