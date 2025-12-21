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
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

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
        try {
            sagaLogRepository.insertIfNotExists(
                    eventMessage.getEventId(),
                    SagaStatus.PENDING.toString(),
                    objectMapper.writeValueAsString(eventMessage.getPayload()));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        int updated = sagaLogRepository.updateStatusIfMatches(
                eventMessage.getEventId(),
                SagaStatus.PENDING,
                SagaStatus.PROCESSING
        );
        if (updated == 0) {
            return;
        }
        try {
            createShippingDetails(eventMessage.getPayload().getUserId(), eventMessage.getPayload().getOrderId());
            int processingUpdated = sagaLogRepository.updateStatusIfMatches(
                    eventMessage.getEventId(),
                    SagaStatus.PROCESSING,
                    SagaStatus.COMPLETED
            );
            EventMessage<Void> eventPublishedMessage = EventMessage.<Void>builder()
                    .eventId(eventMessage.getEventId())
                    .correlationId(eventMessage.getCorrelationId())
                    .eventType("shipping.success")
                    .occurredAt(Instant.now())
                    .source("shipping-service")
                    .payload(null)
                    .build();
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    shippingEventPublisher.publishShippingSuccessEvent(eventPublishedMessage);
                }
            });
        }
        catch (Exception e) {
            sagaLogService.failSaga(eventMessage.getEventId(), eventMessage.getCorrelationId());
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
