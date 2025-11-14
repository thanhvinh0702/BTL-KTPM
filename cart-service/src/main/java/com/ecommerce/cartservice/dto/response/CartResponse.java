package com.ecommerce.cartservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartResponse {
    /**
     * mô tả toàn bộ giỏ hàng
     */
    private String cartId;
    private Long userId;
    private List<CartItemResponse> items;
    private Double totalPrice;
    private Instant createdAt;
    private Instant updatedAt;
}
