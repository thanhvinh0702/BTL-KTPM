package com.ecommerce.cartservice.event.dto;

import com.ecommerce.cartservice.command.model.Cart;
import com.ecommerce.cartservice.command.model.CartItem;
import com.ecommerce.cartservice.dto.external.ProductResponse;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CommandQuerySyncEvent {

    String cartId;
    Long userId;
    CartItem cartItem;
    ProductResponse product;
    EventType type;
}
