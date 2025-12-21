package com.ecommerce.cartservice.command.service;

import com.ecommerce.cartservice.command.model.SagaLog;
import com.ecommerce.cartservice.command.model.SagaStatus;
import com.ecommerce.cartservice.command.repository.SagaLogRepository;
import com.ecommerce.cartservice.event.dto.EventMessage;
import com.ecommerce.cartservice.event.publisher.CartEventPublisher;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class SagaLogService {

    private final SagaLogRepository sagaLogRepository;
    private final ObjectMapper objectMapper;
    private final CartEventPublisher cartEventPublisher;

    public void ensureSagaLogExists(EventMessage<?> eventMessage) {
        try {
            sagaLogRepository.insertIfNotExists(
                    eventMessage.getEventId(),
                    SagaStatus.PENDING.toString(),
                    objectMapper.writeValueAsString(eventMessage.getPayload())
            );
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void failSaga(String sagaId, String correlationId) {

        EventMessage<Void> event = EventMessage.<Void>builder()
                .eventId(sagaId)
                .correlationId(correlationId)
                .eventType("cart.failed")
                .occurredAt(Instant.now())
                .source("cart-service")
                .payload(null)
                .build();
        cartEventPublisher.publishCartEmptyFailed(event);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void failCompensationSaga(String sagaId, String correlationId) {

        EventMessage<Void> event = EventMessage.<Void>builder()
                .eventId(sagaId)
                .correlationId(correlationId)
                .eventType("cart.compensated-failed")
                .occurredAt(Instant.now())
                .source("cart-service")
                .payload(null)
                .build();
        cartEventPublisher.publishCartEmptyCompensatedSuccess(event);
    }
}