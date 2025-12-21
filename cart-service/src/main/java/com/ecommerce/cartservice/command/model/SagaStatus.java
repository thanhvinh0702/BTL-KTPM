package com.ecommerce.cartservice.command.model;

public enum SagaStatus {
    PENDING,
    PROCESSING,
    COMPLETED,
    COMPENSATED,
    FAILED
}
