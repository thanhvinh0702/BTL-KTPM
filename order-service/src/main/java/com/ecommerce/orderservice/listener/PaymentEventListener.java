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
public class PaymentEventListener {

    private final OrderSagaService orderSagaService;
    private final OrdersService ordersService;
    private final OrderEventPublisher orderEventPublisher;

    @RabbitListener(
            queues = "${rabbitmq.queue.payment-success}"
    )
    public void handlePaymentSuccess(EventMessage<Void> eventMessage) {
        orderSagaService.updateSaga(eventMessage.getEventId(), OrderSaga.builder().paymentStatus(SagaStatus.COMPLETED).build());
        log.info("Payment SUCCESS for sagaId={}, orderId={}", eventMessage.getEventId(), eventMessage.getCorrelationId());
        ordersService.tryConfirmedOrder(eventMessage);
    }

    @RabbitListener(
            queues = "${rabbitmq.queue.payment-failed}"
    )
    @Transactional
    public void handlePaymentFailed(EventMessage<Void> eventMessage) {
        orderSagaService.updateSaga(eventMessage.getEventId(), OrderSaga.builder()
                .paymentStatus(SagaStatus.COMPENSATED)
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
        log.info("Payment FAILED for sagaId={}, orderId={}", eventMessage.getEventId(), eventMessage.getCorrelationId());
    }

    @RabbitListener(
            queues = "${rabbitmq.queue.payment-compensated-success}"
    )
    @Transactional
    public void handlePaymentCompensatedSuccess(EventMessage<Void> eventMessage) {
        orderSagaService.updateSaga(eventMessage.getEventId(), OrderSaga.builder()
                .paymentStatus(SagaStatus.COMPENSATED)
                .build());
        log.info("Payment COMPENSATED SUCCESS for sagaId={}, orderId={}", eventMessage.getEventId(), eventMessage.getCorrelationId());
    }
}
