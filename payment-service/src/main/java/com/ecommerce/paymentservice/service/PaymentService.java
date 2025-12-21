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

import java.time.Instant;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final SagaLogRepository sagaLogRepository;
    private final UserCreditService userCreditService;
    private final SagaLogService sagaLogService;
    private final PaymentMapper paymentMapper;
    private final PaymentEventPublisher paymentEventPublisher;
    private final ObjectMapper objectMapper;

    @Transactional
    public void idempotencyPayment(EventMessage<OrderCreatedPayload> eventMessage, PaymentRequest request) {
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
            makePayment(request);
            sagaLog.setStatus(SagaStatus.COMPLETED);
            sagaLogService.save(sagaLog);
            EventMessage<Void> eventPublishedMessage = EventMessage.<Void>builder()
                    .eventId(eventMessage.getEventId())
                    .correlationId(eventMessage.getCorrelationId())
                    .eventType("payment.success")
                    .occurredAt(Instant.now())
                    .source("payment-service")
                    .payload(null)
                    .build();
            paymentEventPublisher.publishPaymentSuccessEvent(eventPublishedMessage);
        }
        catch (Exception e) {
            sagaLog.setStatus(SagaStatus.COMPENSATED);
            sagaLogService.save(sagaLog);
            EventMessage<Void> eventPublishedMessage = EventMessage.<Void>builder()
                    .eventId(eventMessage.getEventId())
                    .correlationId(eventMessage.getCorrelationId())
                    .eventType("payment.failed")
                    .occurredAt(Instant.now())
                    .source("payment-service")
                    .payload(null)
                    .build();
            paymentEventPublisher.publishPaymentFailedEvent(eventPublishedMessage);
            throw e;
        }
    }

    public PaymentResponse makePayment(PaymentRequest request) throws PaymentException {

        // Subtract from user's credit
        userCreditService.modifyUserCredit(request.getUserId(), -request.getAmount());

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
}
