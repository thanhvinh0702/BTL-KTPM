package com.ecommerce.paymentservice.model;

public enum SagaStatus {
    PENDING,
    PROCESSING,
    COMPLETED,
    COMPENSATING,
    COMPENSATED,
    FAILED
}
