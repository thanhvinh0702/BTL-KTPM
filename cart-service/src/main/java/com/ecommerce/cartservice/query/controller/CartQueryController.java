package com.ecommerce.cartservice.query.controller;

import com.ecommerce.cartservice.query.dto.response.CartQueryResponse;

import com.ecommerce.cartservice.query.service.CartQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartQueryController {

    private final CartQueryService cartQueryService;

    @GetMapping("/{userId}")
    public ResponseEntity<CartQueryResponse> getCartByUserId(@PathVariable Long userId) {
        CartQueryResponse cart = cartQueryService.getCartByUserId(userId);
        return ResponseEntity.ok(cart);
    }
}
