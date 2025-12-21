package com.ecommerce.shippingservice.repository;

import com.ecommerce.shippingservice.model.SagaLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SagaLogRepository extends JpaRepository<SagaLog, String> {
}
