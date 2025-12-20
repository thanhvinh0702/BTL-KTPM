package com.ecommerce.paymentservice.service;

import com.ecommerce.paymentservice.model.SagaLog;
import com.ecommerce.paymentservice.model.SagaStatus;
import com.ecommerce.paymentservice.repository.SagaLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SagaLogService {

    private final SagaLogRepository sagaLogRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void save(SagaLog sagaLog) {
        sagaLogRepository.save(sagaLog);
    }
}
