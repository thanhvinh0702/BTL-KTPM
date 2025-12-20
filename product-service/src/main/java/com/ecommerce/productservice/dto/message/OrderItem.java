package com.ecommerce.productservice.dto.message;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderItem {
    private Integer quantity;
    private Long productId;
}
