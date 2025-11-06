package com.ecommerce.cartservice.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddToCartRequest {
    /**
     * dùng khi thêm sản phẩm mới vào giỏ hàng
     * */
    private Long cartId;
    private Long userId;
    private Long productId;
    private Integer quantity;
}
