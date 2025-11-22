package com.ecommerce.cartservice.event.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CartCheckedOutEvent {
    private String eventId;
    private String timestamp;
    private Long userId;
    private List<Item> items;

    @Data
    @AllArgsConstructor
    public static class Item {
        private Long productId;
        private int quantity;
    }
}
