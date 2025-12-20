package com.ecommerce.productservice.repository;

import com.ecommerce.productservice.model.SagaLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SagaLogRepository extends JpaRepository<SagaLog, String> {
}
