package com.ecommerce.cartservice.event.consumer;

import com.ecommerce.cartservice.command.service.CartCommandService;
import com.ecommerce.cartservice.event.dto.EventMessage;
import com.ecommerce.cartservice.event.dto.OrderCreatedPayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class OrderCreatedConsumer {

    private final CartCommandService cartCommandService;

    @RabbitListener(
            queues = "${rabbitmq.queue.cart.order-created}"
    )
    public void handleOrderCreated(EventMessage<OrderCreatedPayload> eventMessage) {
        try {
            cartCommandService.idempotencyEmptyCart(eventMessage, Long.parseLong(eventMessage.getPayload().getUserId()));
            log.info("Cart empty successful for orderId={}, eventId={}",
                    eventMessage.getPayload().getOrderId(),
                    eventMessage.getEventId());

        } catch (Exception ex) {
            log.error("Cart empty failed for orderId={}, eventId={}, reason={}",
                    eventMessage.getPayload().getOrderId(),
                    eventMessage.getEventId(),
                    ex.getMessage(), ex);

        }
    }
}
