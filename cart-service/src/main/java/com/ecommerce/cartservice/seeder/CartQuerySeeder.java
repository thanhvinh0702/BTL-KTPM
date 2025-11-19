package com.ecommerce.cartservice.seeder;

import com.ecommerce.cartservice.command.model.Cart;
import com.ecommerce.cartservice.command.repository.CartCommandRepository;
import com.ecommerce.cartservice.query.model.CartItemQuery;
import com.ecommerce.cartservice.query.model.CartQuery;
import com.ecommerce.cartservice.query.repository.CartQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@Profile("dev")
@RequiredArgsConstructor
public class CartQuerySeeder implements CommandLineRunner {

    private final CartCommandRepository commandRepo;
    private final CartQueryRepository queryRepo;

    @Override
    public void run(String... args) throws Exception {

        long existing = queryRepo.count();
        long totalCommand = commandRepo.count();

        if (existing >= totalCommand && totalCommand > 0) {
            System.out.println("⏭ CartQuerySeeder skipped. Query model is already synced.");
            return;
        }

        System.out.println("🚀 Starting CartQuerySeeder...");

        List<Cart> carts = commandRepo.findAll();
        int batchSize = 1000;

        for (int i = 0; i < carts.size(); i += batchSize) {

            int end = Math.min(i + batchSize, carts.size());
            List<Cart> chunk = carts.subList(i, end);

            List<CartQuery> readModels = chunk.stream()
                    .map(this::toReadModel)
                    .collect(Collectors.toList());

            queryRepo.saveAll(readModels);

            System.out.println("Synced carts_query: " + end + "/" + carts.size());
        }

        System.out.println("🎯 DONE. Total carts_query: " + queryRepo.count());
    }

    private CartQuery toReadModel(Cart cart) {
        double total = cart.getItems().stream()
                .mapToDouble(i -> i.getPriceAtAdd() * i.getQuantity())
                .sum();

        return CartQuery.builder()
                .cartId(cart.getCartId())
                .userId(cart.getUserId())
                .totalPrice(total)
                .items(
                        cart.getItems().stream()
                                .map(i -> CartItemQuery.builder()
                                        .productId(i.getProductId())
                                        .productName(i.getProductName())
                                        .productImage(i.getProductImage())
                                        .priceAtAdd(i.getPriceAtAdd())
                                        .quantity(i.getQuantity())
                                        .build())
                                .collect(Collectors.toList())
                )
                .build();
    }
}
