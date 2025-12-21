package com.ecommerce.shippingservice.listener;

import com.ecommerce.shippingservice.dto.message.EventMessage;
import com.ecommerce.shippingservice.dto.message.OrderCreatedPayload;
import com.ecommerce.shippingservice.service.ShippingDetailsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderConfirmedListener {

    private final ShippingDetailsService shippingDetailsService;

    @RabbitListener(
            queues = "${rabbitmq.queue.shipping.order-confirmed}"
    )
    public void handleOrderConfirmed(EventMessage<OrderCreatedPayload> eventMessage) {
        try {
            shippingDetailsService.idempotencyCreateShippingDetails(eventMessage);
            log.info("Shipping successful for orderId={}, eventId={}",
                    eventMessage.getPayload().getOrderId(),
                    eventMessage.getEventId());

        } catch (Exception ex) {
            log.error("Shipping failed for orderId={}, eventId={}, reason={}",
                    eventMessage.getPayload().getOrderId(),
                    eventMessage.getEventId(),
                    ex.getMessage(), ex);
        }
    }

}
