package com.ecommerce.cartservice.controller;

import com.ecommerce.cartservice.dto.AddProductRequest;
import com.ecommerce.cartservice.model.Cart;
import com.ecommerce.cartservice.service.CartService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Enumeration;

@RestController
@RequestMapping("/carts")
public class CartController {

    @Autowired
    private final CartService cartService;

    @PostMapping("/add-product")
    public ResponseEntity<Cart> addProductToCart(@RequestBody AddProductRequest request) {
        Cart updatedCart = cartService.addProductToCart(request);
        return ResponseEntity.ok(updatedCart);
    }

    @PutMapping("/increase-productQty/{cartId}/{productId}")
    public ResponseEntity<Cart> increaseProductQuantity(
            @PathVariable Long cartId,
            @PathVariable Long productId) {
        Cart updatedCart = cartService.changeProductQuantity(cartId, productId, +1);
        return ResponseEntity.ok(updatedCart);
    }

    @PutMapping("/decrease-productQty/{cartId}/{productId}")
    public ResponseEntity<Cart> decreaseProductQuantity(
            @PathVariable Long cartId,
            @PathVariable Long productId) {
        Cart updatedCart = cartService.changeProductQuantity(cartId, productId, -1);
        return ResponseEntity.ok(updatedCart);
    }

    @GetMapping("/products/{cartId}")
    public ResponseEntity<Cart> getCartProducts(@PathVariable Long cartId) {
        Cart cart = cartService.getCartById(cartId);
        return ResponseEntity.ok(cart);
    }

    @DeleteMapping("/remove-product/{cartId}/{productId}")
    public ResponseEntity<Void> removeProductFromCart(
            @PathVariable Long cartId,
            @PathVariable Long productId) {
        cartService.removeProductFromCart(cartId, productId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/empty-cart/{cartId}")
    public ResponseEntity<Void> emptyCart(@PathVariable Long cartId) {
        cartService.clearCart(cartId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/good-bye")
    public String goodbye(HttpServletRequest request, HttpServletResponse response) {
        Enumeration<String> headerNames = request.getHeaderNames();
        if (headerNames != null) {
            while (headerNames.hasMoreElements()) {
                String headerName = headerNames.nextElement();
                Enumeration<String> headerValues = request.getHeaders(headerName);
                while (headerValues.hasMoreElements()) {
                    String headerValue = headerValues.nextElement();
                    response.addHeader(headerName, headerValue);
                }
            }
        }

        return "Goodbye";
    }
}
