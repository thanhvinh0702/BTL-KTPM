package com.ecommerce.cartservice.command.repository;

import com.ecommerce.cartservice.command.model.SagaLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SagaLogRepository extends JpaRepository<SagaLog, String> {
}
