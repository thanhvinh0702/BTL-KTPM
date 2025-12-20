package com.ecommerce.paymentservice.dto.request;

import com.ecommerce.paymentservice.Enum.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentRequest {
    /**
     * Dùng khi client gửi yêu cầu thanh toán
     */
    private Long orderId;
    private String userId;
    private Double amount;
    private PaymentMethod paymentMethod;
}
