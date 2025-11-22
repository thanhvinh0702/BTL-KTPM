package com.ecommerce.cartservice.event.dto;

import lombok.Data;

@Data
public class OutOfStockEvent {
    private String eventId;
    private String timestamp;

    private Long productId;
}
