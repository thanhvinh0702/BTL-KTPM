package com.ecommerce.paymentservice.repository;

import com.ecommerce.paymentservice.model.SagaLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SagaLogRepository extends JpaRepository<SagaLog, String> {
}
