package com.ecommerce.cartservice.event.publisher;

import com.ecommerce.cartservice.event.dto.CommandQuerySyncEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CartSyncPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.queue.cart.internal}")
    private String cartInternalQueue;

    public void publish(CommandQuerySyncEvent event) {
        rabbitTemplate.convertAndSend(
                cartInternalQueue,
                event
        );
    }
}
