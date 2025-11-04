package com.ecommerce.cartservice.controller;

import com.ecommerce.cartservice.dto.request.AddToCartRequest;
import com.ecommerce.cartservice.dto.response.CartResponse;
import com.ecommerce.cartservice.model.Cart;
import com.ecommerce.cartservice.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    /**
     * Thêm sản phẩm vào giỏ hàng
     * @param request
     * @return
     */
    @PostMapping("/add-product")
    public ResponseEntity<CartResponse> addProductToCart(@Valid @RequestBody AddToCartRequest request) {
        CartResponse updatedCart = cartService.addProductToCart(request);
        return ResponseEntity.ok(updatedCart);
    }

    /**
     * Tăng số lượng sản pham trong giỏ
     * @param cartId
     * @param productId
     * @return
     */
    @PutMapping("/increase-productQty/{cartId}/{productId}")
    public ResponseEntity<CartResponse> increaseProductQuantity(
            @PathVariable Long cartId,
            @PathVariable Long productId) {
        CartResponse updatedCart = cartService.changeProductQuantity(cartId, productId, +1);
        return ResponseEntity.ok(updatedCart);
    }

    /**
     * Giảm số lượng sản phẩm trong giỏ
     * @param cartId
     * @param productId
     * @return
     */
    @PutMapping("/decrease-productQty/{cartId}/{productId}")
    public ResponseEntity<CartResponse> decreaseProductQuantity(
            @PathVariable Long cartId,
            @PathVariable Long productId) {
        CartResponse updatedCart = cartService.changeProductQuantity(cartId, productId, -1);
        return ResponseEntity.ok(updatedCart);
    }

    /**
     * Lấy thông tin toàn bộ sản phẩm trong giỏ hàng
     * @param cartId
     * @return
     */
    @GetMapping("/products/{cartId}")
    public ResponseEntity<CartResponse> getCartById(@PathVariable Long cartId) {
        CartResponse cart = cartService.getCartById(cartId);
        return ResponseEntity.ok(cart);
    }

    /**
     * Xóa 1 sản phẩm khỏi giỏ hàng, sau do tra ve gio hang moi
     * @param cartId
     * @param productId
     * @return
     */
    @DeleteMapping("/remove-product/{cartId}/{productId}")
    public ResponseEntity<CartResponse> removeProductFromCart(
            @PathVariable Long cartId,
            @PathVariable Long productId) {
        CartResponse updatedCart = cartService.removeProductFromCart(cartId, productId);
        return ResponseEntity.ok(updatedCart);
    }

    /**
     * Làm trống toàn bộ giỏ hàng
     * @param cartId
     * @return
     */
    @DeleteMapping("/empty-cart/{cartId}")
    public ResponseEntity<Void> emptyCart(@PathVariable Long cartId) {
        cartService.clearCart(cartId);
        return ResponseEntity.noContent().build();
    }
}
