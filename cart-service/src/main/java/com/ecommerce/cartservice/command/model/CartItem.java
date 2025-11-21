package com.ecommerce.cartservice.command.model;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private Double priceAtAdd;

    @Builder.Default
    private Instant addedAt = Instant.now();
}
