package com.ecommerce.cartservice.event.publisher;

import com.ecommerce.cartservice.event.dto.CartCheckedOutEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CartEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.cart}")
    private String cartExchangeName;

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
}
