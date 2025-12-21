package com.ecommerce.orderservice.listener;

import com.ecommerce.orderservice.dto.EventMessage;
import com.ecommerce.orderservice.model.OrderSaga;
import com.ecommerce.orderservice.model.SagaStatus;
import com.ecommerce.orderservice.publisher.OrderEventPublisher;
import com.ecommerce.orderservice.service.OrderSagaService;
import com.ecommerce.orderservice.service.OrdersService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Slf4j
@Component
@RequiredArgsConstructor
public class CartEventListener {

    private final OrderSagaService orderSagaService;
    private final OrdersService ordersService;
    private final OrderEventPublisher orderEventPublisher;

    @RabbitListener(
            queues = "${rabbitmq.queue.cart-success}"
    )
    public void handleCartSuccess(EventMessage<Void> eventMessage) {
        orderSagaService.updateSaga(eventMessage.getEventId(), OrderSaga.builder().cartStatus(SagaStatus.COMPLETED).build());
        log.info("Cart SUCCESS for sagaId={}, orderId={}", eventMessage.getEventId(), eventMessage.getCorrelationId());
        ordersService.tryConfirmedOrder(eventMessage);
    }

    @RabbitListener(
            queues = "${rabbitmq.queue.cart-failed}"
    )
    @Transactional
    public void handleCartFailed(EventMessage<Void> eventMessage) {
        orderSagaService.updateSaga(eventMessage.getEventId(), OrderSaga.builder()
                .cartStatus(SagaStatus.COMPENSATED)
                .deliveryStatus(SagaStatus.COMPENSATED)
                .build());
        int updated = orderSagaService.markOrderFailedIfNotYet(eventMessage.getEventId());
        if (updated == 1) {
            TransactionSynchronizationManager.registerSynchronization(
                    new TransactionSynchronization() {
                        @Override
                        public void afterCommit() {
                            orderEventPublisher.publishOrderCompensateEvent(eventMessage);
                        }
                    }
            );
        }
        log.info("Cart FAILED for sagaId={}, orderId={}", eventMessage.getEventId(), eventMessage.getCorrelationId());
    }

    @RabbitListener(
            queues = "${rabbitmq.queue.cart-compensated-success}"
    )
    @Transactional
    public void handleCartCompensatedSuccess(EventMessage<Void> eventMessage) {
        orderSagaService.updateSaga(eventMessage.getEventId(), OrderSaga.builder()
                        .cartStatus(SagaStatus.COMPENSATED)
                        .build());
        log.info("Cart COMPENSATED SUCCESS for sagaId={}, orderId={}", eventMessage.getEventId(), eventMessage.getCorrelationId());
    }
}
