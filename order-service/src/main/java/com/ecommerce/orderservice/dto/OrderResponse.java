package com.ecommerce.orderservice.dto;
import lombok.Data;

@Data
public class OrderResponse {
    private Integer orderId;
    private String status;
    private String orderDate;
    private Double orderAmount;
    private String paymentStatus;
}
