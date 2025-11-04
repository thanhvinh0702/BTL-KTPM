package com.ecommerce.cartservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartResponse {
    /**
     * mô tả toàn bộ giỏ hàng
     */
    private Long cartId;
    private Long userId;
    private List<CartItemResponse> items;
    private Double totalAmount; // Tổng tiền giỏ hàng
}
