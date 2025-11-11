package com.ecommerce.paymentservice.dto.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreatedEvent {
    private String eventId;
    private String timestamp;
    private Long orderId;
    private Long userId;
    private BigDecimal totalPrice;
}
