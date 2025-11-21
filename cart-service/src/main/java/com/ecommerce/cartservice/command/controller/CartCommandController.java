package com.ecommerce.cartservice.command.controller;

import com.ecommerce.cartservice.command.dto.request.AddToCartRequest;
import com.ecommerce.cartservice.command.dto.response.CartResponse;
import com.ecommerce.cartservice.command.service.CartCommandService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartCommandController {

    private final CartCommandService cartCommandService;

    /**
     * Thêm sản phẩm vào giỏ hàng
     * @param request
     * @return
     */
    @PostMapping("add-product")
    public ResponseEntity<CartResponse> addProductToCart(@Valid @RequestBody AddToCartRequest request) {
        CartResponse updatedCart = cartCommandService.addProductToCart(request);
        return ResponseEntity.ok(updatedCart);
    }

    @PutMapping("/increase-productQty/{cartId}/{productId}")
    public ResponseEntity<CartResponse> increaseProductQuantity(
            @PathVariable String cartId,
            @PathVariable Long productId) {
        CartResponse updatedCart = cartCommandService.changeProductQuantity(cartId, productId, +1);
        return ResponseEntity.ok(updatedCart);
    }

    @PutMapping("/decrease-productQty/{cartId}/{productId}")
    public ResponseEntity<CartResponse> decreaseProductQuantity(
            @PathVariable String cartId,
            @PathVariable Long productId) {
        CartResponse updatedCart = cartCommandService.changeProductQuantity(cartId, productId, -1);
        return ResponseEntity.ok(updatedCart);
    }

    @DeleteMapping("/remove-product/{cartId}/{productId}")
    public ResponseEntity<CartResponse> removeProductFromCart(
            @PathVariable String cartId,
            @PathVariable Long productId) {
        CartResponse updatedCart = cartCommandService.removeProductFromCart(cartId, productId);
        return ResponseEntity.ok(updatedCart);
    }

    @PutMapping("/{cartId}/checkout")
    public ResponseEntity<Void> checkout(@PathVariable String cartId) {
        cartCommandService.checkout(cartId);
        return ResponseEntity.ok().build();
    }



}
