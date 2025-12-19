package com.ecommerce.cartservice.event.consumer;

import com.ecommerce.cartservice.event.dto.CommandQuerySyncEvent;
import com.ecommerce.cartservice.query.service.CartQueryModelSyncService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CartSyncEventConsumer {

    private final CartQueryModelSyncService cartQueryModelSyncService;

    @RabbitListener(
            queues = "${rabbitmq.queue.cart.internal}",
            concurrency = "1"
    )
    public void handle(CommandQuerySyncEvent commandQuerySyncEvent) {
            cartQueryModelSyncService.syncCartItem(
                    commandQuerySyncEvent.getCartId(),
                    commandQuerySyncEvent.getUserId(),
                    commandQuerySyncEvent.getCartItem(),
                    commandQuerySyncEvent.getProduct(),
                    commandQuerySyncEvent.getType());
    }
}
