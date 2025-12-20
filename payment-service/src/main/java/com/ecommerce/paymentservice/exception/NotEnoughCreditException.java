package com.ecommerce.paymentservice.exception;

public class NotEnoughCreditException extends RuntimeException {
    public NotEnoughCreditException(String message) {
        super(message);
    }
}
