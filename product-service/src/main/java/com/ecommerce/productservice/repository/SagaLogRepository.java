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
    @Query(value = """
    INSERT INTO saga_log (saga_id, saga_status, payload, created_at, updated_at)
    VALUES (:sagaId, :status, :payload, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
    ON CONFLICT (saga_id) DO NOTHING
    """, nativeQuery = true)
    int insertIfNotExists(
            String sagaId,
            String status,
            String payload
    );
}
