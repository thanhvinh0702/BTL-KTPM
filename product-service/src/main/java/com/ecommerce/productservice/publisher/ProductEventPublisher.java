package com.ecommerce.productservice.publisher;

import com.ecommerce.productservice.dto.message.EventMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.order}")
    private String orderExchange;

    @Value("${rabbitmq.routing-key.product.success}")
    private String paymentSuccessRoutingKey;

    @Value("${rabbitmq.routing-key.product.failed}")
    private String paymentFailedRoutingKey;

    public void publishProductSuccessEvent(EventMessage<Void> eventMessage) {
        rabbitTemplate.convertAndSend(orderExchange, paymentSuccessRoutingKey, eventMessage);
        log.info("Published event product success success {} for correlationId={}",
                eventMessage.getEventType(),
                eventMessage.getCorrelationId());
    }

    public void publishProductFailedEvent(EventMessage<Void> eventMessage) {
        rabbitTemplate.convertAndSend(orderExchange, paymentFailedRoutingKey, eventMessage);
        log.info("Published event product failed success {} for correlationId={}",
                eventMessage.getEventType(),
                eventMessage.getCorrelationId());
    }
}
