package com.ecommerce.cartservice.model.command;

import jakarta.validation.constraints.NotNull;
import lombok.*;

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
