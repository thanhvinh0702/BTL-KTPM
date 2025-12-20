package com.ecommerce.orderservice.repository;

import com.ecommerce.orderservice.model.OrderSaga;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface OrderSagaRepository extends JpaRepository<OrderSaga, String> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM OrderSaga s WHERE s.sagaId = :sagaId")
    Optional<OrderSaga> findByIdForUpdate(String sagaId);

    Optional<OrderSaga> findFirstByUserIdOrderByCreatedAtDesc(String userId);
}
