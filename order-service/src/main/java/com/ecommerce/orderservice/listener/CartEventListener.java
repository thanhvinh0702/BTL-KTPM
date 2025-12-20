package com.ecommerce.orderservice.listener;

import com.ecommerce.orderservice.dto.EventMessage;
import com.ecommerce.orderservice.model.OrderSaga;
import com.ecommerce.orderservice.model.SagaStatus;
import com.ecommerce.orderservice.service.OrderSagaService;
import com.ecommerce.orderservice.service.OrdersService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CartEventListener {

    private final OrderSagaService orderSagaService;
    private final OrdersService ordersService;

    @RabbitListener(
            queues = "${rabbitmq.queue.cart-success}"
    )
    public void handleCartSuccess(EventMessage<Void> eventMessage) {
        orderSagaService.updateSaga(eventMessage.getEventId(), OrderSaga.builder().cartStatus(SagaStatus.COMPLETED).build());
        log.info("Cart SUCCESS for sagaId={}, orderId={}", eventMessage.getEventId(), eventMessage.getCorrelationId());
    }

    @RabbitListener(
            queues = "${rabbitmq.queue.cart-failed}"
    )
    public void handleCartFailed(EventMessage<Void> eventMessage) {
        orderSagaService.updateSaga(eventMessage.getEventId(), OrderSaga.builder()
                .cartStatus(SagaStatus.COMPENSATED)
                .deliveryStatus(SagaStatus.COMPENSATED)
                .build());
        ordersService.syncOrderStatus(eventMessage.getEventId());
        log.info("Cart FAILED for sagaId={}, orderId={}", eventMessage.getEventId(), eventMessage.getCorrelationId());
    }
}
