package com.ecommerce.cartservice.service;

import com.ecommerce.cartservice.client.ProductClient;
import com.ecommerce.cartservice.dto.events.CartCheckedOutEvent;
import com.ecommerce.cartservice.dto.external.ProductResponse;
import com.ecommerce.cartservice.dto.request.AddToCartRequest;
import com.ecommerce.cartservice.dto.response.CartResponse;
import com.ecommerce.cartservice.mapper.CartMapper;
import com.ecommerce.cartservice.model.Cart;
import com.ecommerce.cartservice.model.CartItem;
import com.ecommerce.cartservice.repository.CartItemRepository;
import com.ecommerce.cartservice.repository.CartRepository;
import com.ecommerce.cartservice.service.publisher.CartEventPublisher;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductClient productClient;
    private final CartMapper cartMapper;

    private final CartEventPublisher cartEventPublisher;

    /**
     * Thêm sản phẩm vào giỏ hàng
     * @param request
     * @return
     */
    @Transactional
    public CartResponse addProductToCart(AddToCartRequest request){

        // Find Cart by userId
        Cart cart = cartRepository.findById(request.getCartId())
                .orElseGet(() -> {
                    Cart newCart = Cart.builder()
                            .userId(request.getUserId())
                            .totalAmount(0.0)
                            .build();
                    return cartRepository.save(newCart);
                });

        // check if the product in the cart already exists
        Optional<CartItem> existingItemOpt = cart.getItems().stream()
                .filter(item -> item.getProductId().equals(request.getProductId()))
                .findFirst();

        if (existingItemOpt.isPresent()) {
            CartItem existingItem = existingItemOpt.get();
            existingItem.setQuantity(existingItem.getQuantity() + request.getQuantity());
            cartItemRepository.save(existingItem);
        } else {
            // Call ProductService for pricing info
            ProductResponse product = productClient.getProductById(request.getProductId());

            CartItem newItem = CartItem.builder()
                    .productId(product.getId())
                    .productName(product.getName())
                    .priceAtTime(product.getPrice())
                    .quantity(request.getQuantity())
                    .cart(cart)
                    .build();

            cartItemRepository.save(newItem);
            cart.getItems().add(newItem);
        }

        // update total amount
       double totalAmount = cart.getItems().stream()
               .mapToDouble(item -> item.getPriceAtTime() * item.getQuantity())
               .sum();
        cart.setTotalAmount(totalAmount);

        Cart savedCart = cartRepository.save(cart);
        return cartMapper.toCartResponse(savedCart);
    }

    /**
     * Increase/decrease quantity of product in the cart
     * @param cartId
     * @param productId
     * @param delta
     * @return
     */
    @Transactional
    public CartResponse changeProductQuantity(Long cartId, Long productId, int delta) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        CartItem item = cart.getItems().stream()
                .filter(i -> i.getProductId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Product not found in cart"));

        int newQty = item.getQuantity() + delta;
        if (newQty <= 0) {
            cartItemRepository.delete(item);
            cart.getItems().remove(item);
        } else {
            item.setQuantity(newQty);
            cartItemRepository.save(item);
        }

        // Update total amount
        double totalAmount = cart.getItems().stream()
                .mapToDouble(i -> i.getPriceAtTime() * i.getQuantity())
                .sum();
        cart.setTotalAmount(totalAmount);
        Cart savedCart = cartRepository.save(cart);

        return cartMapper.toCartResponse(savedCart);
    }

    /**
     * Delete a product from the cart
     * @param cartId
     * @return
     */
    @Transactional
    public CartResponse removeProductFromCart(Long cartId, Long productId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        cart.getItems().removeIf(item -> item.getProductId().equals(productId));
        cartItemRepository.deleteByCartIdAndProductId(cartId, productId);

        double totalAmount = cart.getItems().stream()
                .mapToDouble(i -> i.getPriceAtTime() * i.getQuantity())
                .sum();
        cart.setTotalAmount(totalAmount);

        Cart savedCart = cartRepository.save(cart);
        return cartMapper.toCartResponse(savedCart);
    }

    /**
     * empty all cart
     */
    @Transactional
    public void clearCart(Long cartId) {
        // Check
        cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        // Delete all
        cartItemRepository.deleteAllByCartId(cartId);

        // update
        Cart cart = cartRepository.findById(cartId).get();
        cart.setTotalAmount(0.0);
        cartRepository.save(cart);
    }


    /**
     * get cart info by ID
     */
    public CartResponse getCartById(Long cartId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));
        return cartMapper.toCartResponse(cart);
    }

    @Transactional
    public void checkout(Long cartId) {
        Cart cart  = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        CartCheckedOutEvent event = new CartCheckedOutEvent();
        event.setUserId(cart.getUserId());
        event.setEventId(UUID.randomUUID().toString());
        event.setTimestamp(Instant.now().toString());

        List<CartCheckedOutEvent.Item> items = cart.getItems().stream()
                .map(i -> {
                    CartCheckedOutEvent.Item ie = new CartCheckedOutEvent.Item();
                    ie.setProductId(i.getProductId());
                    ie.setQuantity(i.getQuantity());
                    return ie;
                })
                .toList();

        event.setItems(items);

        cartEventPublisher.publishCartCheckedOut(event);

        clearCart(cartId);

    }

}


