package com.ecommerce.paymentservice.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserCreditResponse {

    private Long id;
    private String userId;
    private Double credit;
}
