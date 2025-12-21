package com.ecommerce.orderservice.publisher;

import com.ecommerce.orderservice.dto.EventMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.order}")
    private String exchange;

    @Value("${rabbitmq.routing-key.order.created}")
    private String orderCreatedRoutingKey;

    @Value("${rabbitmq.routing-key.shipping.order-confirmed}")
    private String orderConfirmedRoutingKey;

    public void publishOrderCreatedEvent(EventMessage<?> eventMessage) {
        rabbitTemplate.convertAndSend(exchange, orderCreatedRoutingKey, eventMessage);
        log.info("Published event order created {} for correlationId={}",
                eventMessage.getEventType(),
                eventMessage.getCorrelationId());
    }

    public void publishOrderConfirmedEvent(EventMessage<?> eventMessage) {
        rabbitTemplate.convertAndSend(exchange, orderConfirmedRoutingKey, eventMessage);
        log.info("Published event order confirmed {} for correlationId={}",
                eventMessage.getEventType(),
                eventMessage.getCorrelationId());
    }
}
