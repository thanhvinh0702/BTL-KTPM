package com.ecommerce.cartservice.query.model;

import lombok.AllArgsConstructor;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "carts_query")
public class CartQuery {
    @Id
    private String cartId;

    private Long userId;
    private List<CartItemQuery> items;
    private Double totalPrice;
}