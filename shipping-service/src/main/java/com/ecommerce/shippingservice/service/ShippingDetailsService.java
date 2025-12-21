package com.ecommerce.shippingservice.service;

import com.ecommerce.shippingservice.client.UserClient;
import com.ecommerce.shippingservice.dto.client.UserResponse;
import com.ecommerce.shippingservice.dto.message.EventMessage;
import com.ecommerce.shippingservice.dto.message.OrderCreatedPayload;
import com.ecommerce.shippingservice.model.SagaLog;
import com.ecommerce.shippingservice.model.SagaStatus;
import com.ecommerce.shippingservice.model.ShippingDetails;
import com.ecommerce.shippingservice.publisher.ShippingEventPublisher;
import com.ecommerce.shippingservice.repository.SagaLogRepository;
import com.ecommerce.shippingservice.repository.ShippingDetailsRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class ShippingDetailsService {

    private final ShippingDetailsRepository shippingDetailsRepository;
    private final SagaLogRepository sagaLogRepository;
    private final SagaLogService sagaLogService;
    private final UserClient userClient;
    private final ObjectMapper objectMapper;
    private final ShippingEventPublisher shippingEventPublisher;

    @Transactional
    public void idempotencyCreateShippingDetails(EventMessage<OrderCreatedPayload> eventMessage) {
        SagaLog sagaLog = sagaLogRepository.findById(eventMessage.getEventId())
                .orElseGet(() -> {
                    try {
                        return SagaLog.builder()
                                .sagaId(eventMessage.getEventId())
                                .status(SagaStatus.PENDING)
                                .payload(objectMapper.writeValueAsString(eventMessage.getPayload()))
                                .build();
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                });
        if (sagaLog.getStatus().equals(SagaStatus.COMPLETED) || sagaLog.getStatus().equals(SagaStatus.COMPENSATED)) {
            return;
        }
        try {
            createShippingDetails(eventMessage.getPayload().getUserId(), eventMessage.getPayload().getOrderId());
            sagaLog.setStatus(SagaStatus.COMPLETED);
            sagaLogService.save(sagaLog);
            EventMessage<Void> eventPublishedMessage = EventMessage.<Void>builder()
                    .eventId(eventMessage.getEventId())
                    .correlationId(eventMessage.getCorrelationId())
                    .eventType("shipping.success")
                    .occurredAt(Instant.now())
                    .source("shipping-service")
                    .payload(null)
                    .build();
            shippingEventPublisher.publishShippingSuccessEvent(eventPublishedMessage);
        }
        catch (Exception e) {
            sagaLog.setStatus(SagaStatus.COMPENSATED);
            sagaLogService.save(sagaLog);
            EventMessage<Void> eventPublishedMessage = EventMessage.<Void>builder()
                    .eventId(eventMessage.getEventId())
                    .correlationId(eventMessage.getCorrelationId())
                    .eventType("shipping.failed")
                    .occurredAt(Instant.now())
                    .source("shipping-service")
                    .payload(null)
                    .build();
            shippingEventPublisher.publishShippingFailedEvent(eventPublishedMessage);
            throw e;
        }
    }

    public ShippingDetails createShippingDetails(String userId, Long orderId) {
        UserResponse user = userClient.findById(Long.parseLong(userId), userId, "USER");
        ShippingDetails shippingDetails = ShippingDetails.builder()
                .state(user.getAddress().getState())
                .street(user.getAddress().getStreet())
                .city(user.getAddress().getCity())
                .flatNo(user.getAddress().getFlatNo())
                .zipCode(user.getAddress().getZipCode())
                .orderId(orderId)
                .build();
        return shippingDetailsRepository.save(shippingDetails);
    }

}
