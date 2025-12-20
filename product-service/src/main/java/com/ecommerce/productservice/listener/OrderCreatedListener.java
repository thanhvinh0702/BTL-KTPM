package com.ecommerce.productservice.listener;

import com.ecommerce.productservice.dto.message.EventMessage;
import com.ecommerce.productservice.dto.message.OrderCreatedPayload;
import com.ecommerce.productservice.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class OrderCreatedListener {

    private final ProductService productService;

    @RabbitListener(
            queues = "${rabbitmq.queue.product.order-created}"
    )
    public void handleOrderCreatedEvent(EventMessage<OrderCreatedPayload> eventMessage) {
        try {
            productService.idempotencyReserveProduct(eventMessage, eventMessage.getPayload());
            log.info("Product reserved successful for orderId={}, eventId={}, payload={}",
                    eventMessage.getPayload().getOrderId(),
                    eventMessage.getEventId(),
                    eventMessage.getPayload());
        } catch (Exception e) {
            log.error("Product reserved failed for orderId={}, eventId={}, payload={}, reason={}",
                    eventMessage.getPayload().getOrderId(),
                    eventMessage.getEventId(),
                    eventMessage.getPayload(),
                    e.getMessage(), e);
        }
    }
}
