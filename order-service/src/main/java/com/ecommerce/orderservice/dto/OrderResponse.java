package com.ecommerce.orderservice.dto;
import com.ecommerce.orderservice.model.OrderStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Data
public class OrderResponse {
    private Long orderId;
    private OrderStatus status;
    private LocalDateTime orderDate;
    private Double orderAmount;
    private String paymentStatus;
    private List<OrderItemResponse> orderItem;
}
