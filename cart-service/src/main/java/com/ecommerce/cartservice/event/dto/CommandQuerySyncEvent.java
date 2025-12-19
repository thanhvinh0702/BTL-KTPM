package com.ecommerce.cartservice.event.dto;

import com.ecommerce.cartservice.command.model.Cart;
import com.ecommerce.cartservice.dto.external.ProductResponse;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CommandQuerySyncEvent {

    Cart cart;
    ProductResponse product;
}
