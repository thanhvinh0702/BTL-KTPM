package com.ecommerce.cartservice.command.service.publisher;

import com.ecommerce.cartservice.command.dto.event.CartCheckedOutEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CartEventPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final TopicExchange cartExchange;

    @Value("${rabbitmq.routing-key.cart-checked-out}")
    private String routingKey;

    public void publishCartCheckedOut(CartCheckedOutEvent event) {
        rabbitTemplate.convertAndSend(
                cartExchange.getName(),
                routingKey,
                event
        );
    }
}
