package com.ecommerce.cartservice.event.publisher;

import com.ecommerce.cartservice.event.dto.CartCheckedOutEvent;
import com.ecommerce.cartservice.event.dto.EventMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.cart}")
    private String cartExchangeName;

    @Value("${rabbitmq.exchange.order}")
    private String orderExchangeName;

    @Value("${rabbitmq.routing-key.cart.success}")
    private String cartEmptySuccessRoutingKey;

    @Value("${rabbitmq.routing-key.cart.failed}")
    private String cartEmptyFailedRoutingKey;

    @Value("${rabbitmq.routing-key.cart.checked-out}")
    private String cartCheckedOutRoutingKey;

    // Publish event CartCheckedOut
    public void publishCartCheckedOut(CartCheckedOutEvent event) {
        rabbitTemplate.convertAndSend(
                cartExchangeName,
                cartCheckedOutRoutingKey,
                event
        );
    }

    public void publishCartEmptySuccess(EventMessage<Void> eventMessage) {
        rabbitTemplate.convertAndSend(orderExchangeName, cartEmptySuccessRoutingKey, eventMessage);
        log.info("Published event cart clear success {} for correlationId={}",
                eventMessage.getEventType(),
                eventMessage.getCorrelationId());
    }

    public void publishCartEmptyFailed(EventMessage<Void> eventMessage) {
        rabbitTemplate.convertAndSend(orderExchangeName, cartEmptyFailedRoutingKey, eventMessage);
        log.info("Published event cart clear failed {} for correlationId={}",
                eventMessage.getEventType(),
                eventMessage.getCorrelationId());
    }
}
