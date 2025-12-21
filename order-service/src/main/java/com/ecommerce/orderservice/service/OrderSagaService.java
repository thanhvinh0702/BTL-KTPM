package com.ecommerce.orderservice.service;

import com.ecommerce.orderservice.model.OrderSaga;
import com.ecommerce.orderservice.repository.OrderSagaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderSagaService {

    private final OrderSagaRepository orderSagaRepository;

    @Transactional
    public OrderSaga updateSaga(String sagaId, OrderSaga update) {
        Optional<OrderSaga> optionalSaga = orderSagaRepository.findByIdForUpdate(sagaId);

        if (optionalSaga.isEmpty()) {
            throw new IllegalArgumentException("Saga not found: " + sagaId);
        }

        OrderSaga existing = optionalSaga.get();

        if (update.getPaymentStatus() != null) {
            existing.setPaymentStatus(update.getPaymentStatus());
        }
        if (update.getProductStatus() != null) {
            existing.setProductStatus(update.getProductStatus());
        }
        if (update.getCartStatus() != null) {
            existing.setCartStatus(update.getCartStatus());
        }
        if (update.getDeliveryStatus() != null) {
            existing.setDeliveryStatus(update.getDeliveryStatus());
        }

        return orderSagaRepository.save(existing);
    }
    public int markOrderFailedIfNotYet(String sagaId) {
        return orderSagaRepository.markOrderFailedIfNotYet(sagaId);
    }
}
