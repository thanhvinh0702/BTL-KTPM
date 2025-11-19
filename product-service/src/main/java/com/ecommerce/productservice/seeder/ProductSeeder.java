package com.ecommerce.productservice.seeder;

import com.ecommerce.productservice.model.Product;
import com.ecommerce.productservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Profile("dev")
public class ProductSeeder implements CommandLineRunner {

    private final ProductRepository productRepository;

    @Override
    public void run(String... args) throws Exception {
        long count = productRepository.count();
        if (count >= 50000) {
            System.out.println("Products already exist: " + count);
            return;
        }

        int total = 100000;
        int batch = 1000;
        List<Product> buffer = new ArrayList<>();

        for (int i = 1; i <= total; i++) {
            Product product = Product.builder()
                    .name("Product " + i)
                    .categoryName(randomCategory())
                    .description("Description " + i)
                    .imageUrl("https://example.com/images/" + i + ".jpg")
                    .isAvailable(true)
                    .price(Double.valueOf((double) Math.round(Math.random() * 1000)))
                    .quantity(Integer.valueOf((int) (Math.random() * 100)))
                    .ownerId(Long.valueOf((long) (Math.random() * 10000) + 1))
                    .build();

            buffer.add(product);

            if (buffer.size() == batch) {
                productRepository.saveAll(buffer);
                buffer.clear();
                System.out.println("Inserted up to " + i);
            }
        }

        if (!buffer.isEmpty()) {
            productRepository.saveAll(buffer);
        }

        System.out.println("DONE. Total products: " + productRepository.count());
    }

    private String randomCategory() {
        String[] arr = {"Electronics", "Fashion", "Books", "Home", "Sports"};
        return arr[(int) (Math.random() * arr.length)];
    }
}

