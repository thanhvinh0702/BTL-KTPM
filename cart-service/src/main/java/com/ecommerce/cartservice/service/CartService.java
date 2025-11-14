package com.ecommerce.cartservice.service;

import com.ecommerce.cartservice.client.ProductClient;
import com.ecommerce.cartservice.dto.events.CartCheckedOutEvent;
import com.ecommerce.cartservice.dto.external.ProductResponse;
import com.ecommerce.cartservice.dto.request.AddToCartRequest;
import com.ecommerce.cartservice.dto.response.CartResponse;
import com.ecommerce.cartservice.mapper.CartMapper;
import com.ecommerce.cartservice.model.command.Cart;
import com.ecommerce.cartservice.model.command.CartItem;
import com.ecommerce.cartservice.repository.CartRepository;
import com.ecommerce.cartservice.service.publisher.CartEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final ProductClient productClient;
    private final CartMapper cartMapper;

    private final CartEventPublisher cartEventPublisher;

    /**
     * Add product to cart (MongoDB)
     */
    public CartResponse addProductToCart(AddToCartRequest request) {

        // Find or create cart
        Cart cart = cartRepository.findByUserId(request.getUserId())
                .orElseGet(() -> cartRepository.save(
                        Cart.builder()
                                .userId(request.getUserId())
                                .items(new ArrayList<>())
                                .build()
                ));

        // Check if product already exists in embedded array
        CartItem existingItem = cart.getItems().stream()
                .filter(i -> i.getProductId().equals(request.getProductId()))
                .findFirst()
                .orElse(null);

        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + request.getQuantity());
        } else {
            // Call ProductService
            ProductResponse product = productClient.getProductById(request.getProductId());

            CartItem newItem = CartItem.builder()
                    .productId(product.getId())
                    .quantity(request.getQuantity())
                    .PriceAtAdd(product.getPrice())
                    .productName(product.getName())
                    .productImage(product.getImageUrl())
                    .addedAt(Instant.now())
                    .build();

            cart.getItems().add(newItem);
        }

        cartRepository.save(cart);
        return cartMapper.toCartResponse(cart);
    }

    /**
     * Change product quantity
     */
    public CartResponse changeProductQuantity(String cartId, Long productId, int delta) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        CartItem item = cart.getItems().stream()
                .filter(i -> i.getProductId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Product not found"));

        int newQty = item.getQuantity() + delta;

        if (newQty <= 0) {
            cart.getItems().remove(item);
        } else {
            item.setQuantity(newQty);
        }

        return cartMapper.toCartResponse(cartRepository.save(cart));
    }

    /**
     * Remove a product
     */
    public CartResponse removeProductFromCart(String cartId, Long productId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        cart.getItems().removeIf(i -> i.getProductId().equals(productId));

        return cartMapper.toCartResponse(cartRepository.save(cart));
    }


    /**
     * Clear entire cart
     */
    public void clearCart(String cartId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        cart.getItems().clear();
        cartRepository.save(cart);
    }

    /**
     * Get cart by ID
     */
    public CartResponse getCartById(String cartId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));


        return cartMapper.toCartResponse(cart);
    }

    public CartResponse getCartByUserId(Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        return cartMapper.toCartResponse(cart);
    }

    /**
     * Checkout event
     */
    public void checkout(String cartId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        CartCheckedOutEvent event = new CartCheckedOutEvent();
        event.setEventId(UUID.randomUUID().toString());
        event.setTimestamp(Instant.now().toString());
        event.setUserId(cart.getUserId());

        List<CartCheckedOutEvent.Item> items = cart.getItems().stream()
                .map(i -> new CartCheckedOutEvent.Item(i.getProductId(), i.getQuantity()))
                .toList();

        event.setItems(items);

        cartEventPublisher.publishCartCheckedOut(event);
        clearCart(cartId);
    }


}
