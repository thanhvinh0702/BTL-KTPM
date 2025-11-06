package com.ecommerce.shippingservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ShippingRequest {

    @NotBlank
    private String address;

    @NotBlank
    private String city;

    @NotBlank
    private String state;

    @NotBlank
    private String postalCode;
}
