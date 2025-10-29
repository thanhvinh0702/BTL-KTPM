package com.ecommerce.cartservice.service;

import com.ecommerce.cartservice.dto.AddProductRequest;
import com.ecommerce.cartservice.model.Cart;
import com.ecommerce.cartservice.model.CartItem;
import com.ecommerce.cartservice.repository.CartItemRepository;
import com.ecommerce.cartservice.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    public Cart addProductToCart(AddProductRequest request){

        // Tìm cart theo userId
        Optional<Cart> existingCartOpt = cartRepository.findByUserId(request.getUserId());
        Cart cart = existingCartOpt.orElseGet(() -> {
            Cart newCart = Cart.builder()
                    .userId(request.getUserId())
                    .totalAmount(0.0)
                    .build();
            return cartRepository.save(newCart);
        });

        CartItem existingItem = cart.getItems().stream()
                .filter(i -> i.getProductId().equals(request.getProductId()))
                .findFirst()
                .orElse(null);

        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + request.getQuantity());
            existingItem.setSubtotal(existingItem.getQuantity() * existingItem.getPriceAtTime());
        } else {
            CartItem newItem = CartItem.builder()
                    .productId(request.getProductId())
                    .productName(request.getProductName())
                    .priceAtTime(request.getPrice())
                    .quantity(request.getQuantity())
                    .subtotal(request.getPrice() * request.getQuantity())
                    .cart(cart)
                    .build();
            cart.getItems().add(newItem);
        }

        // Cập nhật tổng tiền
        cart.setTotalAmount(cart.getItems().stream().mapToDouble(CartItem::getSubtotal).sum());
        return cartRepository.save(cart);
    }

    public Cart changeProductQuantity(Long cartId, Long productId, int delta) {
        Cart cart = getCartById(cartId);

        cart.getItems().forEach(item -> {
            if (item.getProductId().equals(productId)) {
                int newQty = item.getQuantity() + delta;
                if (newQty <= 0) {
                    cartItemRepository.removeProductFromCart(cartId, productId);
                } else {
                    item.setQuantity(newQty);
                    item.setSubtotal(item.getPriceAtTime() * newQty);
                }
            }
        });

        // Nếu cart rỗng → xóa
        if (cartItemRepository.countByCart_CartId(cartId) == 0) {
            cartRepository.delete(cart);
            return null;
        }

        // Cập nhật tổng tiền
        cart.setTotalAmount(cart.getItems().stream().mapToDouble(CartItem::getSubtotal).sum());
        return cartRepository.save(cart);
    }

    public Cart getCartById(Long cartId) {
        return cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart not found with id " + cartId));
    }

    public void removeProductFromCart(Long cartId, Long productId) {
        cartItemRepository.removeProductFromCart(cartId, productId);
        if (cartItemRepository.countByCart_CartId(cartId) == 0) {
            cartRepository.deleteById(cartId);
        }
    }

    public void clearCart(Long cartId) {
        cartItemRepository.removeAllProductsFromCart(cartId);
        cartRepository.deleteById(cartId);
    }
}


