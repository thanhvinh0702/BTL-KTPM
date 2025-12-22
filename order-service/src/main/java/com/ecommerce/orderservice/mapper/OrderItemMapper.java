package com.ecommerce.orderservice.mapper;

import com.ecommerce.orderservice.dto.OrderItemResponse;
import com.ecommerce.orderservice.model.OrderItem;
import lombok.Builder;
import lombok.Data;
import org.springframework.stereotype.Component;

@Component
public class OrderItemMapper {

    public OrderItemResponse toResponse(OrderItem orderItem) {
        return OrderItemResponse.builder()
                .productId(orderItem.getProductId())
                .quantity(orderItem.getQuantity())
                .price(orderItem.getPrice())
                .build();
    }
}
