package com.ecommerce.cartservice.command.service;

import com.ecommerce.cartservice.client.ProductClient;
import com.ecommerce.cartservice.command.dto.request.AddToCartRequest;
import com.ecommerce.cartservice.command.dto.response.CartResponse;
import com.ecommerce.cartservice.command.mapper.CartMapper;
import com.ecommerce.cartservice.command.model.Cart;
import com.ecommerce.cartservice.command.model.CartItem;
import com.ecommerce.cartservice.command.repository.CartCommandRepository;
import com.ecommerce.cartservice.dto.external.ProductResponse;
import com.ecommerce.cartservice.event.dto.CartCheckedOutEvent;
import com.ecommerce.cartservice.event.publisher.CartEventPublisher;
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

    private final ProductClient productClient;

    private final CartQueryModelSyncService cartQueryModelSyncService;

    private final CartEventPublisher cartEventPublisher;

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

        // Call ProductService
        ProductResponse product = productClient.getProductById(request.getProductId());
        if (product == null || Boolean.FALSE.equals(product.getIsAvailable())) {
            throw new RuntimeException("Product is not available");
        }

        // Check if product already exists in embedded array
        CartItem existingItem = cart.getItems().stream()
                .filter(i -> i.getProductId().equals(request.getProductId()))
                .findFirst()
                .orElse(null);

        int requestedQty = request.getQuantity();

        if (existingItem != null) {
            int newQuantity = existingItem.getQuantity() + requestedQty;

            // Validate with ProductService's stock
            if (newQuantity > product.getQuantity()) {
                throw new RuntimeException("Not enough stock");
            }

            // Update quantity
            existingItem.setQuantity(newQuantity);

        } else {

            // Validate for NEW item
            if (requestedQty > product.getQuantity()) {
                throw new RuntimeException("Not enough stock");
            }

            // Create new CartItem
            CartItem newItem = CartItem.builder()
                    .productId(product.getId())
                    .quantity(requestedQty)
                    .priceAtAdd(product.getPrice())
                    .addedAt(Instant.now())
                    .build();

            cart.getItems().add(newItem);
        }

        Cart saved = cartCommandRepository.save(cart);

        cartQueryModelSyncService.sync(saved, product);

        return CartMapper.toResponse(saved);
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
                .orElseThrow(() -> new RuntimeException("Product not in cart"));

        int newQty = item.getQuantity() + delta;
        // Validate if delta > 0
        if (delta > 0) {
            ProductResponse product = productClient.getProductById(productId);

            if (product == null || Boolean.FALSE.equals(product.getIsAvailable())) {
                throw new RuntimeException("Product is not available");
            }

            if (newQty > product.getQuantity()) {
                throw new RuntimeException("Not enough stock");
            }
        }

        // Apply quantity change
        if (newQty <= 0) {
            cart.getItems().remove(item);
        } else {
            item.setQuantity(newQty);
        }

        Cart saved = cartCommandRepository.save(cart);

        cartQueryModelSyncService.sync(saved);

        return CartMapper.toResponse(saved);
    }
    /**
     * Remove a product
     */
    public CartResponse removeProductFromCart(String cartId, Long productId) {
        Cart cart = cartCommandRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        cart.getItems().removeIf(i -> i.getProductId().equals(productId));

        Cart saved = cartCommandRepository.save(cart);

        cartQueryModelSyncService.sync(saved);

        return CartMapper.toResponse(saved);
    }

    /**
     * Clear entire cart
     */
    public void clearCart(String cartId) {
        Cart cart = cartCommandRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        cart.getItems().clear();

        Cart saved = cartCommandRepository.save(cart);

        cartQueryModelSyncService.sync(saved);
    }

    public void checkout(String cartId) {
        Cart cart = cartCommandRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        // Validate từng item bằng ProductService
        for (CartItem i : cart.getItems()) {
            ProductResponse p = productClient.getProductById(i.getProductId());

            if (p == null || Boolean.FALSE.equals(p.getIsAvailable())) {
                throw new RuntimeException("Product " + i.getProductId() + " is unavailable");
            }

            if (p.getQuantity() < i.getQuantity()) {
                throw new RuntimeException("Not enough stock for product " + i.getProductId());
            }
        }

        // Build event
        CartCheckedOutEvent event = CartCheckedOutEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .timestamp(Instant.now().toString())
                .userId(cart.getUserId())
                .items(
                        cart.getItems().stream()
                                .map(i -> new CartCheckedOutEvent.Item(
                                        i.getProductId(),
                                        i.getQuantity()
                                ))
                                .toList()
                )
                .build();

        // Publish event
        cartEventPublisher.publishCartCheckedOut(event);

        // Clear cart
        cart.getItems().clear();
        Cart saved = cartCommandRepository.save(cart);
        cartQueryModelSyncService.sync(saved);
    }

}
