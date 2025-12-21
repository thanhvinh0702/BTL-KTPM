package com.ecommerce.shippingservice.service;

import com.ecommerce.shippingservice.dto.message.EventMessage;
import com.ecommerce.shippingservice.model.SagaLog;
import com.ecommerce.shippingservice.publisher.ShippingEventPublisher;
import com.ecommerce.shippingservice.repository.SagaLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class SagaLogService {

    private final SagaLogRepository sagaLogRepository;
    private final ShippingEventPublisher shippingEventPublisher;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void save(SagaLog sagaLog) {
        sagaLogRepository.save(sagaLog);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void failSaga(String sagaId, String correlationId) {

        EventMessage<Void> event = EventMessage.<Void>builder()
                .eventId(sagaId)
                .correlationId(correlationId)
                .eventType("shipping.failed")
                .occurredAt(Instant.now())
                .source("shipping-service")
                .payload(null)
                .build();
        shippingEventPublisher.publishShippingFailedEvent(event);
    }

}
