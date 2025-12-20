package com.ecommerce.orderservice.mapper;

import com.ecommerce.orderservice.dto.EventMessage;
import com.ecommerce.orderservice.dto.OrderPlacedMessage;
import com.ecommerce.orderservice.model.Orders;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class OrderMapper {

    public OrderPlacedMessage toOrderPlacedMessage(Orders order) {
        return OrderPlacedMessage.builder()
                .orderId(order.getId())
                .totalAmount(order.getTotalAmount())
                .userId(order.getUserId())
                .orderItems(order.getOrderItem())
                .build();
    }

    public <T> EventMessage<T> toEventMessage(
            String eventId,
            String eventType,
            String correlationId,
            T payload
    ) {
        return EventMessage.<T>builder()
                .eventId(eventId)
                .eventType(eventType)
                .occurredAt(Instant.now())
                .source("order-service")
                .correlationId(correlationId)
                .payload(payload)
                .build();
    }
}