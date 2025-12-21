package com.ecommerce.paymentservice.service;

import com.ecommerce.paymentservice.dto.event.EventMessage;
import com.ecommerce.paymentservice.model.SagaLog;
import com.ecommerce.paymentservice.model.SagaStatus;
import com.ecommerce.paymentservice.publisher.PaymentEventPublisher;
import com.ecommerce.paymentservice.repository.SagaLogRepository;
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
    private final PaymentEventPublisher paymentEventPublisher;

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
                .eventType("payment.failed")
                .occurredAt(Instant.now())
                .source("payment-service")
                .payload(null)
                .build();
        paymentEventPublisher.publishPaymentFailedEvent(event);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void failCompensationSaga(String sagaId, String correlationId) {

        EventMessage<Void> event = EventMessage.<Void>builder()
                .eventId(sagaId)
                .correlationId(correlationId)
                .eventType("payment.compensated-failed")
                .occurredAt(Instant.now())
                .source("payment-service")
                .payload(null)
                .build();
        paymentEventPublisher.publishPaymentCompensatedSuccessEvent(event);
    }
}
