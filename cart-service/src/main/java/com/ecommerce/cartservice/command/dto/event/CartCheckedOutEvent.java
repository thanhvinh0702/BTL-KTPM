package com.ecommerce.cartservice.command.dto.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartCheckedOutEvent implements Serializable {

    private String evenId;
    private String timestamp;
    private Long userId;
    private List<Item> items;


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Item {
        private Long productId;
        private int quantity;
    }
}
