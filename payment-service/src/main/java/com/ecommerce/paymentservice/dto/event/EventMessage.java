package com.ecommerce.paymentservice.dto.event;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class EventMessage<T> {

    private String eventId;
    private String eventType;
    private Instant occurredAt;
    private String source;
    private String correlationId;
    private T payload;
}