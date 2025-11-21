package com.ecommerce.cartservice.seeder;

import com.ecommerce.cartservice.command.model.Cart;
import com.ecommerce.cartservice.command.model.CartItem;
import com.ecommerce.cartservice.command.repository.CartCommandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
@Profile("dev")
@RequiredArgsConstructor
public class CartSeeder implements CommandLineRunner {

    private final CartCommandRepository cartRepository;
    private final Random random = new Random();

    private static final int TOTAL_CARTS = 100000;
    private static final int MAX_PRODUCT_ID = 100000;

    @Override
    public void run(String... args) {

        long existing = cartRepository.count();
        if (existing >= TOTAL_CARTS) {
            System.out.println("⏭ Seeder skipped. Existing carts: " + existing);
            return;
        }

        System.out.println("🚀 Starting CartSeeder...");
        Instant now = Instant.now(); // optimize

        int batchSize = 1000;
        List<Cart> buffer = new ArrayList<>(batchSize);

        for (int i = 1; i <= TOTAL_CARTS; i++) {

            Cart cart = Cart.builder()
                    .userId((long) i)
                    .items(generateRandomItems(now))   // random items
                    .createdAt(now)
                    .updatedAt(now)
                    .build();

            buffer.add(cart);

            if (buffer.size() == batchSize) {
                cartRepository.saveAll(buffer);
                buffer.clear();

                System.out.println("Inserted: " + i + " carts...");
            }
        }

        // insert last batch
        if (!buffer.isEmpty()) {
            cartRepository.saveAll(buffer);
        }

        System.out.println("🎯 DONE. Total carts inserted: " + cartRepository.count());
    }

    /**
     * Generate 1–5 random items
     */
    private List<CartItem> generateRandomItems(Instant now) {
        int count = random.nextInt(5) + 1;

        List<CartItem> items = new ArrayList<>(count);

        for (int i = 0; i < count; i++) {
            long productId = random.nextInt(MAX_PRODUCT_ID) + 1;

            items.add(
                    CartItem.builder()
                            .productId(productId)
                            .quantity(random.nextInt(5) + 1)
                            .priceAtAdd((random.nextDouble() * 500) + 10) // 10–510
                            .addedAt(now)
                            .build()
            );
        }

        return items;
    }
}
