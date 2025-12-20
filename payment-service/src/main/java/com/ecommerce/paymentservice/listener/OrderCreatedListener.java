package com.ecommerce.paymentservice.listener;

import com.ecommerce.paymentservice.dto.event.EventMessage;
import com.ecommerce.paymentservice.dto.event.OrderCreatedPayload;
import com.ecommerce.paymentservice.dto.request.PaymentRequest;
import com.ecommerce.paymentservice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderCreatedListener {

    private final PaymentService paymentService;

    @RabbitListener(
            queues = "${rabbitmq.queue.payment.order-created}"
    )
    public void handleOrderCreated(EventMessage<OrderCreatedPayload> eventMessage) {
        try {
            PaymentRequest paymentRequest = PaymentRequest.builder()
                    .orderId(eventMessage.getPayload().getOrderId())
                    .userId(eventMessage.getPayload().getUserId())
                    .amount(eventMessage.getPayload().getTotalAmount())
                    .build();

            paymentService.idempotencyPayment(eventMessage, paymentRequest);
            log.info("Payment successful for orderId={}, eventId={}",
                    eventMessage.getPayload().getOrderId(),
                    eventMessage.getEventId());

        } catch (Exception ex) {
            log.error("Payment failed for orderId={}, eventId={}, reason={}",
                    eventMessage.getPayload().getOrderId(),
                    eventMessage.getEventId(),
                    ex.getMessage(), ex);
        }
    }

}
