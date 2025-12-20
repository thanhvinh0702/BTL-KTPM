package com.ecommerce.paymentservice.publisher;

import com.ecommerce.paymentservice.dto.event.EventMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.order}")
    private String orderExchange;

    @Value("${rabbitmq.routing-key.payment.success}")
    private String paymentSuccessRoutingKey;

    @Value("${rabbitmq.routing-key.payment.failed}")
    private String paymentFailedRoutingKey;

    public void publishPaymentSuccessEvent(EventMessage<Void> eventMessage) {
        rabbitTemplate.convertAndSend(orderExchange, paymentSuccessRoutingKey, eventMessage);
        log.info("Published event payment success success {} for correlationId={}",
                eventMessage.getEventType(),
                eventMessage.getCorrelationId());
    }

    public void publishPaymentFailedEvent(EventMessage<Void> eventMessage) {
        rabbitTemplate.convertAndSend(orderExchange, paymentFailedRoutingKey, eventMessage);
        log.info("Published event payment failed success {} for correlationId={}",
                eventMessage.getEventType(),
                eventMessage.getCorrelationId());
    }
}
