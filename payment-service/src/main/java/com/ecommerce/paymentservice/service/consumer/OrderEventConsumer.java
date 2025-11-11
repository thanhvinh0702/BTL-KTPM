package com.ecommerce.paymentservice.service.consumer;

import com.ecommerce.paymentservice.config.RabbitMQConfig;
import com.ecommerce.paymentservice.dto.event.OrderCreatedEvent;
import com.ecommerce.paymentservice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderEventConsumer {

    private final PaymentService paymentService;

    @RabbitListener(queues = "${rabbitmq.payment.order.created.queue}")
    public void handleOrderCreated(OrderCreatedEvent event) {
        log.info("ReceivedOrderCreated: {}", event);
        paymentService.processPayment(event);
    }


}
