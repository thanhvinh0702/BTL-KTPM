package com.ecommerce.cartservice.event.dto;

import lombok.Data;

@Data
public class LowStockEvent {
    private String eventId;
    private String timestamp;
    private Long productId;
    private Integer stock;
}
