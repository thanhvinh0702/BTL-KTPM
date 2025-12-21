package com.ecommerce.cartservice.command.service;

import com.ecommerce.cartservice.client.ProductClient;
import com.ecommerce.cartservice.command.dto.request.AddToCartRequest;
import com.ecommerce.cartservice.command.dto.response.CartResponse;
import com.ecommerce.cartservice.command.mapper.CartMapper;
import com.ecommerce.cartservice.command.model.Cart;
import com.ecommerce.cartservice.command.model.CartItem;
import com.ecommerce.cartservice.command.model.SagaLog;
import com.ecommerce.cartservice.command.model.SagaStatus;
import com.ecommerce.cartservice.command.repository.CartCommandRepository;
import com.ecommerce.cartservice.command.repository.CartItemCommandRepository;
import com.ecommerce.cartservice.command.repository.SagaLogRepository;
import com.ecommerce.cartservice.dto.external.ProductResponse;
import com.ecommerce.cartservice.event.dto.*;
import com.ecommerce.cartservice.event.publisher.CartEventPublisher;
import com.ecommerce.cartservice.event.publisher.CartSyncPublisher;
import com.ecommerce.cartservice.exception.ConflictException;
import com.ecommerce.cartservice.exception.NotFoundException;
import com.ecommerce.cartservice.query.service.CartQueryModelSyncService;
import com.ecommerce.cartservice.security.RequireOwner;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CartCommandService {
    private final CartCommandRepository cartCommandRepository;

    private final CartItemCommandRepository cartItemCommandRepository;

    private final ProductClient productClient;

    private final CartQueryModelSyncService cartQueryModelSyncService;

    private final CartEventPublisher cartEventPublisher;

    private final CartSyncPublisher cartSyncPublisher;

    private final SagaLogRepository sagaLogRepository;
    private final SagaLogService sagaLogService;
    private final ObjectMapper objectMapper;

    /**
     * Add product to cart
     */
    public CartResponse addProductToCart(AddToCartRequest request) {
        // Find or create cart
        Cart cart = cartCommandRepository.findByUserId(request.getUserId())
                .orElseGet(() -> cartCommandRepository.save(
                        Cart.builder()
                                .id(UUID.randomUUID().toString())
                                .userId(request.getUserId())
                                .items(new ArrayList<>())
                                .build()
                ));

        // Call ProductService
        ProductResponse product = productClient.getProductById(request.getProductId());
        System.out.println(product);
        if (product == null) {
            throw new NotFoundException("Product is not found");
        }
        if (Boolean.FALSE.equals(product.getIsAvailable())) {
            throw new ConflictException("Product is not available");
        }

        // Check if product already exists
        CartItem existingItem = cartItemCommandRepository.findByCartIdAndProductId(cart.getId(), request.getProductId());

        int requestedQty = request.getQuantity();

        if (existingItem != null) {
            int newQuantity = existingItem.getQuantity() + requestedQty;

            // Validate with ProductService's stock
            if (newQuantity > product.getQuantity()) {
                throw new ConflictException("Not enough stock");
            }

            // Update quantity
            existingItem.setQuantity(newQuantity);
        } else {

            // Validate for NEW item
            if (requestedQty > product.getQuantity()) {
                throw new ConflictException("Not enough stock");
            }

            // Create new CartItem
            existingItem = CartItem.builder()
                    .productId(product.getId())
                    .quantity(requestedQty)
                    .priceAtAdd(product.getPrice())
                    .cart(cart)
                    .addedAt(Instant.now())
                    .build();
        }

        cartItemCommandRepository.save(existingItem);

        // Sync command-query
        cartSyncPublisher.publish(CommandQuerySyncEvent.builder()
                        .cartId(cart.getId())
                        .userId(cart.getUserId())
                        .cartItem(existingItem)
                        .product(product)
                        .type(EventType.ADD)
                        .build());

        return CartMapper.toResponse(cart);
    }

    /**
     * Change product quantity
     */
    public CartResponse changeProductQuantity(String cartId, Long productId, int delta) {

        Cart cart = cartCommandRepository.findById(cartId)
                .orElseThrow(() -> new NotFoundException("Cart not found"));

        CartItem item = cartItemCommandRepository.findByCartIdAndProductId(cartId, productId);

        if (item == null) {
            throw new ConflictException("Product not in cart");
        }

        int newQty = item.getQuantity() + delta;
        // Validate if delta > 0
        if (delta > 0) {
            ProductResponse product = productClient.getProductById(productId);
            if (product == null) {
                throw new NotFoundException("Product is not found");
            }

            if (Boolean.FALSE.equals(product.getIsAvailable())) {
                throw new ConflictException("Product is not available");
            }

            if (newQty > product.getQuantity()) {
                throw new ConflictException("Not enough stock");
            }
        }

        // Apply quantity change
        if (newQty <= 0) {
            cart.getItems().remove(item);
        } else {
            item.setQuantity(newQty);
        }

        cartItemCommandRepository.save(item);

        cartSyncPublisher.publish(CommandQuerySyncEvent.builder()
                .cartId(cart.getId())
                .userId(cart.getUserId())
                .cartItem(item)
                .type(EventType.UPDATE)
                .build());

        return CartMapper.toResponse(cart);
    }
    /**
     * Remove a product
     */
    public CartResponse removeProductFromCart(String cartId, Long productId) {
        Cart cart = cartCommandRepository.findById(cartId)
                .orElseThrow(() -> new NotFoundException("Cart not found"));
        CartItem item = cartItemCommandRepository.findByCartIdAndProductId(cartId, productId);
        cartItemCommandRepository.delete(item);
        cartSyncPublisher.publish(CommandQuerySyncEvent.builder()
                        .cartId(cart.getId())
                        .userId(cart.getUserId())
                        .cartItem(item)
                        .type(EventType.DELETE)
                        .build());

        return CartMapper.toResponse(cart);
    }

    /**
     * Clear entire cart
     */
    public void clearCart(String cartId) {
        Cart cart = cartCommandRepository.findById(cartId)
                .orElseThrow(() -> new NotFoundException("Cart not found"));

        cartItemCommandRepository.deleteAllByCartId(cart.getId());

        cartSyncPublisher.publish(CommandQuerySyncEvent.builder()
                    .cartId(cart.getId())
                    .userId(cart.getUserId())
                    .type(EventType.CLEAR)
                    .build());
    }

    @Transactional
    public void idempotencyEmptyCart(EventMessage<OrderCreatedPayload> eventMessage, Long userId) {
        SagaLog sagaLog = sagaLogRepository.findById(eventMessage.getEventId()).orElseGet(() ->
        {
            try {
                return SagaLog.builder()
                        .sagaId(eventMessage.getEventId())
                        .status(SagaStatus.PENDING)
                        .payload(objectMapper.writeValueAsString(eventMessage.getPayload()))
                        .build();
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });
        try {
            emptyCart(userId);
            sagaLog.setStatus(SagaStatus.COMPLETED);
            sagaLogService.save(sagaLog);
            EventMessage<Void> eventPublishedMessage = EventMessage.<Void>builder()
                    .eventId(eventMessage.getEventId())
                    .correlationId(eventMessage.getCorrelationId())
                    .eventType("cart.success")
                    .occurredAt(Instant.now())
                    .source("cart-service")
                    .payload(null)
                    .build();
            cartEventPublisher.publishCartEmptySuccess(eventPublishedMessage);
        }
        catch (Exception e) {
            sagaLog.setStatus(SagaStatus.COMPENSATED);
            sagaLogService.save(sagaLog);
            EventMessage<Void> eventPublishedMessage = EventMessage.<Void>builder()
                    .eventId(eventMessage.getEventId())
                    .correlationId(eventMessage.getCorrelationId())
                    .eventType("cart.failed")
                    .occurredAt(Instant.now())
                    .source("cart-service")
                    .payload(null)
                    .build();
            cartEventPublisher.publishCartEmptyFailed(eventPublishedMessage);
            throw e;
        }
    }

    public void emptyCart(Long userId) {
        Cart cart = cartCommandRepository.findByUserId(userId).orElseThrow(() ->
                new RuntimeException("No cart found for user " + userId));
        clearCart(cart.getId());
    }

    public void checkout(String cartId) {
        Cart cart = cartCommandRepository.findById(cartId)
                .orElseThrow(() -> new NotFoundException("Cart not found"));

        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new ConflictException("Cart is empty");
        }

        // Validate từng item bằng ProductService
        for (CartItem i : cart.getItems()) {
            ProductResponse p = productClient.getProductById(i.getProductId());

            if (p == null) {
                throw new NotFoundException("Product " + i.getProductId() + " is not found");
            }

            if (Boolean.FALSE.equals(p.getIsAvailable())) {
                throw new NotFoundException("Product " + i.getProductId() + " is unavailable");
            }

            if (p.getQuantity() < i.getQuantity()) {
                throw new ConflictException("Not enough stock for product " + i.getProductId());
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
        cartSyncPublisher.publish(CommandQuerySyncEvent.builder().cartId(saved.getId()).userId(saved.getUserId()).type(EventType.CLEAR).build());
    }

}
