package com.ecommerce.cartservice.event.handler;


import com.ecommerce.cartservice.event.dto.BackInStockEvent;
import com.ecommerce.cartservice.event.dto.LowStockEvent;
import com.ecommerce.cartservice.event.dto.OutOfStockEvent;
import com.ecommerce.cartservice.event.dto.ProductUpdatedEvent;
import com.ecommerce.cartservice.query.model.CartItemQuery;
import com.ecommerce.cartservice.query.model.CartQuery;
import com.ecommerce.cartservice.query.repository.CartQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CartProductSyncHandler {
    private final CartQueryRepository cartQueryRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    public void handeProductUpdate(ProductUpdatedEvent event) {
        System.out.print("Dang hanle prodcut update");
        List<CartQuery> carts = cartQueryRepository.findByItemsProductId(event.getProductId());

        System.out.print(carts);

        for (CartQuery cart : carts) {
            boolean updated = false;

            for (CartItemQuery item : cart.getItems()) {
                if (item.getProductId().equals(event.getProductId())) {
                    item.setProductName(event.getName());
                    item.setProductImage(event.getImageUrl());

                if (!item.getPriceAtAdd().equals(event.getPrice())) {
                    item.setPriceAtAdd(event.getPrice());
                }

                updated = true;
                }
            }

            if (updated) {
                // recalc total price correctly
                double total = cart.getItems().stream()
                                .mapToDouble(i -> i.getPriceAtAdd() * i.getQuantity())
                                        .sum();
                cart.setTotalPrice(total);
                cartQueryRepository.save(cart);
                redisTemplate.delete("cart:" + cart.getUserId());
            }
        }

    }

    public void handleOutOfStock(OutOfStockEvent event) {
        List<CartQuery> carts = cartQueryRepository.findByItemsProductId(event.getProductId());

        for (CartQuery cart : carts) {
            cart.getItems().removeIf(item -> item.getProductId().equals(event.getProductId()));

            cartQueryRepository.save(cart);
            redisTemplate.delete("cart:" + cart.getUserId());
        }
    }

    public void handleBackInStock(BackInStockEvent event) {
        List<CartQuery> carts = cartQueryRepository.findByItemsProductId(event.getProductId());

        for (CartQuery cart : carts) {
            cart.getItems().forEach(item -> {
                if (item.getProductId().equals(event.getProductId())) {
                    item.setIsAvailable(true);
                }
            });

            cartQueryRepository.save(cart);
            redisTemplate.delete("cart:" + cart.getUserId());
        }
    }

    public void handleLowStock(LowStockEvent event) {
        List<CartQuery> carts = cartQueryRepository.findByItemsProductId(event.getProductId());

        for (CartQuery cart : carts) {
            cart.getItems().forEach(item -> {
                if (item.getProductId().equals(event.getProductId())
                        && item.getQuantity() > event.getStock()) {
                    item.setQuantity(event.getStock());
                }
            });

            cartQueryRepository.save(cart);
            redisTemplate.delete("cart:" + cart.getUserId());
        }
    }
}
