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
        if (commandQuerySyncEvent.getProduct() == null) {
            cartQueryModelSyncService.sync(commandQuerySyncEvent.getCart());
        }
        else {
            cartQueryModelSyncService.sync(commandQuerySyncEvent.getCart(), commandQuerySyncEvent.getProduct());
        }
    }
}
