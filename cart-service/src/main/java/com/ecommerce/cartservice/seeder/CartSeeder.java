package com.ecommerce.cartservice.seeder;

import com.ecommerce.cartservice.model.command.Cart;
import com.ecommerce.cartservice.model.command.CartItem;
import com.ecommerce.cartservice.repository.CartRepository;
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
    private final CartRepository cartRepository;
    private final Random random = new Random();

    private static final int TOTAL_CARTS = 100000;
    private static final int MAX_PRODUCT_ID = 100000;
    private static final int MAX_USER_ID = 100000;


    @Override
    public void run(String... args) throws Exception {

        long current = cartRepository.count();
        if (current >= TOTAL_CARTS) {
            System.out.println("Seeder skipped. Carts already exist: " + current);
            return;
        }

        System.out.print("===========Starting CartSeeder...================");

        int batchSize = 1000;
        List<Cart> buffer = new ArrayList<>(batchSize);

        for (int i = 1; i <= TOTAL_CARTS; i++) {
            Cart cart = Cart.builder()
                    .userId((long) i)
                    .items(generateRandomItems())
                    .createdAt(Instant.now())
                    .updatedAt(Instant.now())
                    .build();

            buffer.add(cart);

            if (buffer.size() == batchSize) {
                cartRepository.saveAll(buffer);
                buffer.clear();
                System.out.println("Inserted " + i + " carts...");
            }
        }

        // insert last batch
        if (!buffer.isEmpty()) {
            cartRepository.saveAll(buffer);
        }

        System.out.println("🎯 DONE. Total carts: " + cartRepository.count());
    }

    private Long generateRandomUserId() {
        return (long) (random.nextInt(MAX_USER_ID) + 1);
    }

    private List<CartItem> generateRandomItems() {
        int itemCount = random.nextInt(5) + 1; // 1 to 5 items
        List<CartItem> list = new ArrayList<>(itemCount);

        for (int i = 0; i < itemCount; i++) {
            long productId = random.nextInt(MAX_PRODUCT_ID) + 1; // 1 -> 100000

            list.add(CartItem.builder()
                    .productId(productId)
                    .quantity(random.nextInt(5) + 1)
                    .PriceAtAdd((random.nextDouble() * 500) + 10) // 10 → 510
                    .productName("Product " + productId)
                    .productImage("https://example.com/img/" + productId + ".jpg")
                    .addedAt(Instant.now())
                    .build());
        }

        return list;
    }
}


