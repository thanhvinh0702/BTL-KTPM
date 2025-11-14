package com.ecommerce.cartservice.model;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.mongodb.core.index.Indexed;

import java.time.Instant;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItem {

    @NotNull
    private Long productId;

    @NotNull
    private Integer quantity;

    @NotNull
    private Double PriceAtAdd;

    private String productName;
    private String productImage;

    private Instant addedAt = Instant.now();

}
