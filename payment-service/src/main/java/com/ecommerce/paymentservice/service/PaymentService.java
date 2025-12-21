package com.ecommerce.paymentservice.service;

import com.ecommerce.paymentservice.Enum.PaymentMethod;
import com.ecommerce.paymentservice.Enum.PaymentStatus;
import com.ecommerce.paymentservice.dto.event.EventMessage;
import com.ecommerce.paymentservice.dto.event.OrderCreatedPayload;
import com.ecommerce.paymentservice.dto.request.PaymentRequest;
import com.ecommerce.paymentservice.dto.response.PaymentResponse;
import com.ecommerce.paymentservice.exception.PaymentException;
import com.ecommerce.paymentservice.mapper.PaymentMapper;
import com.ecommerce.paymentservice.model.Payment;
import com.ecommerce.paymentservice.model.SagaLog;
import com.ecommerce.paymentservice.model.SagaStatus;
import com.ecommerce.paymentservice.publisher.PaymentEventPublisher;
import com.ecommerce.paymentservice.repository.PaymentRepository;
import com.ecommerce.paymentservice.repository.SagaLogRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final SagaLogRepository sagaLogRepository;
    private final UserCreditService userCreditService;
    private final PaymentMapper paymentMapper;
    private final PaymentEventPublisher paymentEventPublisher;
    private final ObjectMapper objectMapper;

    @Transactional
    public void idempotencyPayment(EventMessage<OrderCreatedPayload> eventMessage, PaymentRequest request) {
        try {
            sagaLogRepository.insertIfNotExists(
                    eventMessage.getEventId(),
                    SagaStatus.PENDING,
                    objectMapper.writeValueAsString(eventMessage.getPayload())
            );
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
            makePayment(request);
            int processingUpdated = sagaLogRepository.updateStatusIfMatches(
                    eventMessage.getEventId(),
                    SagaStatus.PROCESSING,
                    SagaStatus.COMPLETED
            );
            EventMessage<Void> eventPublishedMessage = EventMessage.<Void>builder()
                    .eventId(eventMessage.getEventId())
                    .correlationId(eventMessage.getCorrelationId())
                    .eventType("payment.success")
                    .occurredAt(Instant.now())
                    .source("payment-service")
                    .payload(null)
                    .build();
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    paymentEventPublisher.publishPaymentSuccessEvent(eventPublishedMessage);
                }
            });
        }
        catch (Exception e) {
            sagaLogRepository.updateStatusIfMatches(
                    eventMessage.getEventId(),
                    SagaStatus.PROCESSING,
                    SagaStatus.COMPENSATED
            );
            EventMessage<Void> eventPublishedMessage = EventMessage.<Void>builder()
                    .eventId(eventMessage.getEventId())
                    .correlationId(eventMessage.getCorrelationId())
                    .eventType("payment.failed")
                    .occurredAt(Instant.now())
                    .source("payment-service")
                    .payload(null)
                    .build();
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    paymentEventPublisher.publishPaymentFailedEvent(eventPublishedMessage);
                }
            });
            throw e;
        }
    }

    @Transactional
    public void idempotencyPaymentCompensation(String sagaId) {
        // No log case -> no compensation
        int nullUpdated = sagaLogRepository.insertIfNotExists(
                sagaId,
                SagaStatus.COMPENSATED,
                null
        );
        if (nullUpdated == 1) {
            return;
        }
        // Pending case -> no compensation
        int pendingUpdated = sagaLogRepository.updateStatusIfMatches(sagaId, SagaStatus.PENDING, SagaStatus.COMPENSATED);
        if (pendingUpdated == 1) {
            return;
        }
        // Processing case -> no compensation but throw error
        if (sagaLogRepository.existsBySagaIdAndStatus(sagaId, SagaStatus.PROCESSING)) {
            throw new RuntimeException("Saga is still processing");
        }
        // Completed case -> compensation
        int completedUpdated = sagaLogRepository.updateStatusIfMatches(sagaId, SagaStatus.COMPLETED, SagaStatus.COMPENSATED);
        if (completedUpdated == 0) {
            return;
        }
        // Safely compensate here
        SagaLog sagaLog = sagaLogRepository.findById(sagaId)
                .orElseThrow();
        if (sagaLog.getPayload() == null) {
            return;
        }
        OrderCreatedPayload payload = null;
        try {
            payload = objectMapper.readValue(sagaLog.getPayload(), OrderCreatedPayload.class);
            refundPayment(PaymentRequest.builder()
                    .orderId(payload.getOrderId())
                    .amount(payload.getTotalAmount())
                    .userId(payload.getUserId())
                    .build());
            EventMessage<Void> eventPublishedMessage = EventMessage.<Void>builder()
                    .eventId(sagaId)
                    .correlationId(payload.getOrderId().toString())
                    .eventType("payment.compensated-success")
                    .occurredAt(Instant.now())
                    .source("payment-service")
                    .payload(null)
                    .build();
            paymentEventPublisher.publishPaymentCompensatedSuccessEvent(eventPublishedMessage);
        } catch (Exception e) {
            sagaLogRepository.updateStatusIfMatches(
                    sagaId,
                    SagaStatus.COMPENSATED,
                    SagaStatus.FAILED
            );
            EventMessage<Void> eventPublishedMessage = EventMessage.<Void>builder()
                    .eventId(sagaId)
                    .correlationId(payload.getOrderId().toString())
                    .eventType("payment.compensated-failed")
                    .occurredAt(Instant.now())
                    .source("payment-service")
                    .payload(null)
                    .build();
            paymentEventPublisher.publishPaymentCompensatedFailedEvent(eventPublishedMessage);
            throw new RuntimeException(e);
        }
    }

    public PaymentResponse refundPayment(PaymentRequest request) throws PaymentException {

        // Subtract from user's credit
        userCreditService.modifyUserCredit(request.getUserId(), request.getAmount());

        // create payment record
        Payment payment = Payment.builder()
                .orderId(request.getOrderId())
                .userId(request.getUserId())
                .paymentAmount(request.getAmount())
                .paymentDate(LocalDateTime.now())
                .paymentMethod(
                        request.getPaymentMethod() != null
                                ? request.getPaymentMethod()
                                : PaymentMethod.UPI
                )
                .paymentStatus(PaymentStatus.SUCCESSFUL)
                .build();

        Payment savedPayment = paymentRepository.save(payment);
        return paymentMapper.toPaymentResponse(savedPayment);
    }

    public PaymentResponse makePayment(PaymentRequest request) throws PaymentException {

        // Subtract from user's credit
        userCreditService.modifyUserCredit(request.getUserId(), -request.getAmount());

        // create payment record
        Payment payment = Payment.builder()
                .orderId(request.getOrderId())
                .userId(request.getUserId())
                .paymentAmount(-request.getAmount())
                .paymentDate(LocalDateTime.now())
                .paymentMethod(
                        request.getPaymentMethod() != null
                            ? request.getPaymentMethod()
                                : PaymentMethod.UPI
                )
                .paymentStatus(PaymentStatus.SUCCESSFUL)
                .build();

        Payment savedPayment = paymentRepository.save(payment);
        return paymentMapper.toPaymentResponse(savedPayment);
    }
}
