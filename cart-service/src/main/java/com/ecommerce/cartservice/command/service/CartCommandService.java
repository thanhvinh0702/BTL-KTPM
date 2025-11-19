package com.ecommerce.cartservice.command.service;

import com.ecommerce.cartservice.client.ProductClient;
import com.ecommerce.cartservice.command.dto.event.CartCheckedOutEvent;
import com.ecommerce.cartservice.command.dto.request.AddToCartRequest;
import com.ecommerce.cartservice.command.dto.response.CartResponse;
import com.ecommerce.cartservice.command.mapper.CartMapper;
import com.ecommerce.cartservice.command.model.Cart;
import com.ecommerce.cartservice.command.model.CartItem;
import com.ecommerce.cartservice.command.repository.CartCommandRepository;
import com.ecommerce.cartservice.command.dto.external.ProductResponse;
import com.ecommerce.cartservice.command.service.publisher.CartEventPublisher;
import com.ecommerce.cartservice.query.service.CartQueryModelSyncService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CartCommandService {
    private final CartCommandRepository cartCommandRepository;

    private final CartEventPublisher cartEventPublisher;

    private final ProductClient productClient;

    private final CartQueryModelSyncService cartQueryModelSyncService;
    /**
     * Add product to cart
     */
    public CartResponse addProductToCart(AddToCartRequest request) {
        // Find or create cart
        Cart cart = cartCommandRepository.findByUserId(request.getUserId())
                .orElseGet(() -> cartCommandRepository.save(
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
                    .priceAtAdd(product.getPrice())
                    .productName(product.getName())
                    .productImage(product.getImageUrl())
                    .addedAt(Instant.now())
                    .build();

            cart.getItems().add(newItem);
        }

        cartCommandRepository.save(cart);

        cartQueryModelSyncService.sync(cart);

        return CartMapper.toResponse(cart);
    }

    /**
     * Change product quantity
     */
    public CartResponse changeProductQuantity(String cartId, Long productId, int delta) {
        Cart cart = cartCommandRepository.findById(cartId)
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

        cartQueryModelSyncService.sync(cart);

        return CartMapper.toResponse(cartCommandRepository.save(cart));
    }

    /**
     * Remove a product
     */
    public CartResponse removeProductFromCart(String cartId, Long productId) {
        Cart cart = cartCommandRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        cart.getItems().removeIf(i -> i.getProductId().equals(productId));

        cartQueryModelSyncService.sync(cart);

        return CartMapper.toResponse(cartCommandRepository.save(cart));
    }

    /**
     * Clear entire cart
     */
    public void clearCart(String cartId) {
        Cart cart = cartCommandRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        cart.getItems().clear();

        cartCommandRepository.save(cart);

        cartQueryModelSyncService.sync(cart);
    }

    /**
     * Checkout event
     */
    public void checkout(String cartId) {
        Cart cart = cartCommandRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        CartCheckedOutEvent event = new CartCheckedOutEvent();
        event.setEvenId(UUID.randomUUID().toString());
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
