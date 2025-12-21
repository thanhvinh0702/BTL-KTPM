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
public class ShippingEventListener {

    private final OrderSagaService orderSagaService;
    private final OrdersService ordersService;
    private final OrderEventPublisher orderEventPublisher;

    @RabbitListener(
            queues = "${rabbitmq.queue.shipping-success}"
    )
    public void handleShippingSuccess(EventMessage<Void> eventMessage) {
        orderSagaService.updateSaga(eventMessage.getEventId(), OrderSaga.builder().deliveryStatus(SagaStatus.COMPLETED).build());
        log.info("Shipping SUCCESS for sagaId={}, orderId={}", eventMessage.getEventId(), eventMessage.getCorrelationId());
    }

    @RabbitListener(
            queues = "${rabbitmq.queue.shipping-failed}"
    )
    @Transactional
    public void handleShippingFailed(EventMessage<Void> eventMessage) {
        orderSagaService.updateSaga(eventMessage.getEventId(), OrderSaga.builder()
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
        log.info("Shipping FAILED for sagaId={}, orderId={}", eventMessage.getEventId(), eventMessage.getCorrelationId());
    }
}
