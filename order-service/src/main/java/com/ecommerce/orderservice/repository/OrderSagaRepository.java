package com.ecommerce.orderservice.repository;

import com.ecommerce.orderservice.model.OrderSaga;
import com.ecommerce.orderservice.model.SagaStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface OrderSagaRepository extends JpaRepository<OrderSaga, String> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM OrderSaga s WHERE s.sagaId = :sagaId")
    Optional<OrderSaga> findByIdForUpdate(String sagaId);

    @Modifying
    @Query("""
    UPDATE OrderSaga s
    SET s.orderStatus = 'CONFIRMED'
    WHERE s.sagaId = :sagaId
    AND s.orderStatus <> 'CONFIRMED'
    AND s.cartStatus = 'COMPLETED'
    AND s.productStatus = 'COMPLETED'
    AND s.paymentStatus = 'COMPLETED'
    """)
    int confirmOrderIfReady(String sagaId);

    Optional<OrderSaga> findFirstByUserIdOrderByCreatedAtDesc(String userId);
}
