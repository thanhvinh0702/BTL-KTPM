package com.ecommerce.productservice.service;

import com.ecommerce.productservice.dto.message.EventMessage;
import com.ecommerce.productservice.model.SagaLog;
import com.ecommerce.productservice.model.SagaStatus;
import com.ecommerce.productservice.publisher.ProductEventPublisher;
import com.ecommerce.productservice.repository.SagaLogRepository;
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
    private final ProductEventPublisher productEventPublisher;

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
                .eventType("product.failed")
                .occurredAt(Instant.now())
                .source("product-service")
                .payload(null)
                .build();
        productEventPublisher.publishProductFailedEvent(event);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void failCompensatedSaga(String sagaId, String correlationId) {

        EventMessage<Void> event = EventMessage.<Void>builder()
                .eventId(sagaId)
                .correlationId(correlationId)
                .eventType("product.compensated-failed")
                .occurredAt(Instant.now())
                .source("product-service")
                .payload(null)
                .build();
        productEventPublisher.publishCompensatedProductSuccessEvent(event);
    }
}