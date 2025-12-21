package com.ecommerce.shippingservice.dto.message;

import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class OrderCreatedPayload {

    private Long orderId;
    private String userId;
}
