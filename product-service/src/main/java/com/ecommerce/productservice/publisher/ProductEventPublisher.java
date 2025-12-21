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
    private String productSuccessRoutingKey;

    @Value("${rabbitmq.routing-key.product.failed}")
    private String productFailedRoutingKey;

    @Value("${rabbitmq.routing-key.product.compensated-success}")
    private String productCompensationSuccessRoutingKey;

    @Value("${rabbitmq.routing-key.product.compensated-failed}")
    private String productCompensationFailedRoutingKey;

    public void publishProductSuccessEvent(EventMessage<Void> eventMessage) {
        rabbitTemplate.convertAndSend(orderExchange, productSuccessRoutingKey, eventMessage);
        log.info("Published event product success success {} for correlationId={}",
                eventMessage.getEventType(),
                eventMessage.getCorrelationId());
    }

    public void publishProductFailedEvent(EventMessage<Void> eventMessage) {
        rabbitTemplate.convertAndSend(orderExchange, productFailedRoutingKey, eventMessage);
        log.info("Published event product failed success {} for correlationId={}",
                eventMessage.getEventType(),
                eventMessage.getCorrelationId());
    }

    public void publishCompensatedProductSuccessEvent(EventMessage<Void> eventMessage) {
        rabbitTemplate.convertAndSend(orderExchange, productCompensationSuccessRoutingKey, eventMessage);
        log.info("Published event compensate product success success {} for correlationId={}",
                eventMessage.getEventType(),
                eventMessage.getCorrelationId());
    }

    public void publishCompensatedProductFailedEvent(EventMessage<Void> eventMessage) {
        rabbitTemplate.convertAndSend(orderExchange, productCompensationFailedRoutingKey, eventMessage);
        log.info("Published event compensate product failed success {} for correlationId={}",
                eventMessage.getEventType(),
                eventMessage.getCorrelationId());
    }
}
