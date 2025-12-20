package com.ecommerce.paymentservice.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserCreditRequest {

    @NotNull(message = "amount must not be null")
    private Double amount;
}
