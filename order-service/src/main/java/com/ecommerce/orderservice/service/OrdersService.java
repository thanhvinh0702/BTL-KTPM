package com.ecommerce.orderservice.service;

import java.time.LocalDateTime;
import java.util.*;

import com.ecommerce.orderservice.dto.*;
import com.ecommerce.orderservice.mapper.OrderMapper;
import com.ecommerce.orderservice.model.*;
import com.ecommerce.orderservice.publisher.OrderEventPublisher;
import com.ecommerce.orderservice.repository.OrderRepository;
import com.ecommerce.orderservice.client.CartClient;
import com.ecommerce.orderservice.repository.OrderSagaRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;


@Service
@RequiredArgsConstructor
@Slf4j
public class OrdersService {

    private final OrderRepository orderRepository;
    private final CartClient cartClient;
    private final OrderMapper orderMapper;
    private final OrderEventPublisher orderEventPublisher;
    private final OrderSagaRepository orderSagaRepository;

    @Transactional
    public Orders placeOrder(String userId){
        // Check currently processing order
        Optional<OrderSaga> latestSagaOpt = orderSagaRepository.findFirstByUserIdOrderByCreatedAtDesc(userId);

        if (latestSagaOpt.isPresent()) {
            OrderSaga latestSaga = latestSagaOpt.get();

            boolean isFinish =
                    (latestSaga.getPaymentStatus() == SagaStatus.COMPLETED
                    && latestSaga.getCartStatus() == SagaStatus.COMPLETED
                    && latestSaga.getProductStatus() == SagaStatus.COMPLETED
                    && latestSaga.getDeliveryStatus() == SagaStatus.COMPLETED)
                    || (latestSaga.getPaymentStatus() == SagaStatus.COMPENSATED
                    && latestSaga.getCartStatus() == SagaStatus.COMPENSATED
                    && latestSaga.getProductStatus() == SagaStatus.COMPENSATED
                    && latestSaga.getDeliveryStatus() == SagaStatus.COMPENSATED);

            if (!isFinish) {
                throw new IllegalStateException("Previous order still processing. Try again later.");
            }
        }

        // Get User Cart
        CartResponse cartResponse = cartClient.getCartByUserId(userId);

        if(cartResponse.getItems().isEmpty()){
            throw new RuntimeException("No items in order");
        }

        // Create order
        Orders order = new Orders();
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING);
        List<OrderItem> orderItems = cartResponse.getItems().stream()
                .map(ci -> OrderItem.builder()
                        .orders(order)
                        .price(ci.getPriceAtAdd())
                        .quantity(ci.getQuantity())
                        .productId(ci.getProductId())
                        .build())
                .toList();
        order.setUserId(userId);
        order.setOrderItem(orderItems);
        order.setTotalAmount(cartResponse.getTotalPrice());
        orderRepository.save(order);

        // Publish event order created
        String sagaId = UUID.randomUUID().toString();
        OrderSaga orderSaga = OrderSaga.builder()
                .sagaId(sagaId)
                .orderId(order.getId())
                .userId(userId)
                .paymentStatus(SagaStatus.PENDING)
                .productStatus(SagaStatus.PENDING)
                .cartStatus(SagaStatus.PENDING)
                .orderStatus(OrderStatus.PENDING)
                .deliveryStatus(SagaStatus.PENDING)
                .build();
        orderSagaRepository.save(orderSaga);

        OrderPlacedMessage orderPlacedMessage = orderMapper.toOrderPlacedMessage(order);
        EventMessage<OrderPlacedMessage> eventMessage = orderMapper.toEventMessage(sagaId, "order.created", order.getId().toString(), orderPlacedMessage);
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                orderEventPublisher.publishOrderCreatedEvent(eventMessage);
            }
        });
        return order;
    };

    @Transactional
    public void tryConfirmedOrder(EventMessage<?> eventMessage) {
        log.info("Try to confirmed order!");
        int update = orderSagaRepository.confirmOrderIfReady(eventMessage.getEventId());
        if (update == 0) {
            return;
        }
        OrderSaga orderSaga = orderSagaRepository.findById(eventMessage.getEventId()).orElseThrow(() ->
                new NoSuchElementException("No saga found!"));
        OrderPlacedMessage orderPlacedMessage = OrderPlacedMessage.builder()
                .orderId(orderSaga.getOrderId())
                .userId(orderSaga.getUserId())
                .build();
        EventMessage<OrderPlacedMessage> publishedMessage = orderMapper.toEventMessage(
                eventMessage.getEventId(),
                "order.confirmed",
                eventMessage.getCorrelationId(),
                orderPlacedMessage);
        orderEventPublisher.publishOrderConfirmedEvent(publishedMessage);
    }

    @Transactional
    public void compensatingOrder(Long orderId) {
        Orders order = orderRepository.findById(orderId).orElse(null);
        if (order == null) {
            return;
        }
        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
    }

    /**
     * GET METHOD
     */
    public List<OrderResponse> getAllOrder() {
        return orderRepository.findAll().stream().map(orderMapper::toResponse).toList();
    }

    public List<OrderResponse> getAllOrderByUserId(String userId) {
        return orderRepository.findAllByUserId(userId).stream().map(orderMapper::toResponse).toList();
    }

    public OrderResponse getOrderById(Long orderId) {
        return orderMapper.toResponse(orderRepository.findById(orderId).orElseThrow());
    }

    public List<OrderResponse> getAllOrderByDate(LocalDateTime date) {
        List<Orders> orders = orderRepository.findByOrderDateGreaterThanEqual(date);
        return orders.stream().map(orderMapper::toResponse).toList();
    }

    /**
     * DELETE METHOD
     */
    public void deleteOrder(String userId, Long orderId) {
        Orders orders = orderRepository.findById(orderId).orElseThrow();
        if (!orders.getUserId().equals(userId)) {
            throw new AccessDeniedException("Insufficient permission");
        }
        orderRepository.delete(orders);
    }

}
