package com.ecommerce.shippingservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ShippingRequest {

    private String state;
    private String street;
    private String city;
    private String flatNo;
    private String zipCode;
    private Long orderId;
}
