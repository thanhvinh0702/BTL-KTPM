package com.ecommerce.cartservice.query.model;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItemQuery {

    private Long productId;
    private Integer quantity;
    private Double priceAtAdd;
    private String productName;
    private String productImage;
}
