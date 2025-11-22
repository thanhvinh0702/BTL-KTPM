package com.ecommerce.cartservice.event.dto;

import lombok.Data;

@Data
public class ProductUpdatedEvent {
    private String eventId;
    private String timestamp;

    private Long productId;
    private String name;
    private String imageUrl;
    private Double price;
}
