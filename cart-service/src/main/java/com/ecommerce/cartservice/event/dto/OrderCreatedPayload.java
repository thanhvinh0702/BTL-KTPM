package com.ecommerce.cartservice.event.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class OrderCreatedPayload {

    private Long orderId;
    private double totalAmount;
    private String userId;
    private List<OrderItem> orderItems;
}
