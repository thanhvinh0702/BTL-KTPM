package com.ecommerce.cartservice.event.consumer;

import com.ecommerce.cartservice.event.dto.BackInStockEvent;
import com.ecommerce.cartservice.event.dto.LowStockEvent;
import com.ecommerce.cartservice.event.dto.OutOfStockEvent;
import com.ecommerce.cartservice.event.dto.ProductUpdatedEvent;
import com.ecommerce.cartservice.event.handler.CartProductSyncHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductEventConsumer {

    private final CartProductSyncHandler handler;

    @RabbitListener(queues = "${rabbitmq.queue.product.updated}")
    public void handeProductUpdate(ProductUpdatedEvent event) {
        handler.handeProductUpdate(event);
    }

    @RabbitListener(queues = "${rabbitmq.queue.product.out-of-stock}")
    public void handleOutOfStock(OutOfStockEvent event) {
        handler.handleOutOfStock(event);
    }

    @RabbitListener(queues = "${rabbitmq.queue.product.back-in-stock}")
    public void handleBackInStock(BackInStockEvent event) {
        handler.handleBackInStock(event);
    }

    @RabbitListener(queues = "${rabbitmq.queue.product.low-stock}")
    public void handleLowStock(LowStockEvent event) {
        handler.handleLowStock(event);
    }
}
