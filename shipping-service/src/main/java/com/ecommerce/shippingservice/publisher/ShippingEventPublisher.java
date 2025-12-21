package com.ecommerce.shippingservice.publisher;

import com.ecommerce.shippingservice.dto.message.EventMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ShippingEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.order}")
    private String orderExchange;

    @Value("${rabbitmq.routing-key.shipping.success}")
    private String shippingSuccessRoutingKey;

    @Value("${rabbitmq.routing-key.shipping.failed}")
    private String shippingFailedRoutingKey;

    public void publishShippingSuccessEvent(EventMessage<Void> eventMessage) {
        rabbitTemplate.convertAndSend(orderExchange, shippingSuccessRoutingKey, eventMessage);
        log.info("Published event shipping success success {} for correlationId={}",
                eventMessage.getEventType(),
                eventMessage.getCorrelationId());
    }

    public void publishShippingFailedEvent(EventMessage<Void> eventMessage) {
        rabbitTemplate.convertAndSend(orderExchange, shippingFailedRoutingKey, eventMessage);
        log.info("Published event shipping failed success {} for correlationId={}",
                eventMessage.getEventType(),
                eventMessage.getCorrelationId());
    }
}
