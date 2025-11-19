package com.ecommerce.cartservice.command.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddToCartRequest {
    private String cartId;
    private Long userId;
    private Long productId;
    private Integer quantity;
}
