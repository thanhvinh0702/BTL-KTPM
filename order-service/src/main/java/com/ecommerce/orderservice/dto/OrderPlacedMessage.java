package com.ecommerce.orderservice.dto;

import com.ecommerce.orderservice.model.OrderItem;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class OrderPlacedMessage {

    private Long orderId;
    private double totalAmount;
    private String userId;
    private List<OrderItem> orderItems;
}
