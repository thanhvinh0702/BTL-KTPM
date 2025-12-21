package com.ecommerce.productservice.repository;

import com.ecommerce.productservice.model.SagaLog;
import com.ecommerce.productservice.model.SagaStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface SagaLogRepository extends JpaRepository<SagaLog, String> {
    @Modifying
    @Query("""
    UPDATE SagaLog s
    SET s.status = :newStatus
    WHERE s.sagaId = :sagaId
    AND s.status = :expectedStatus
    """)
    int updateStatusIfMatches(
            String sagaId,
            SagaStatus expectedStatus,
            SagaStatus newStatus
    );

    boolean existsBySagaIdAndStatus(String sagaId, SagaStatus status);

    @Modifying
    @Query("""
    INSERT INTO SagaLog (sagaId, status, payload, createdAt, updatedAt)
    SELECT :sagaId, :status, :payload, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
    WHERE NOT EXISTS (
        SELECT 1 FROM SagaLog s WHERE s.sagaId = :sagaId
    )
    """)
    int insertIfNotExists(
            String sagaId,
            SagaStatus status,
            String payload
    );
}
