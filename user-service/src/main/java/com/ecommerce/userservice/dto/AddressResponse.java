package com.ecommerce.userservice.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AddressResponse {
    private String street;
    private String state;
    private String city;
    private String flatNo;
    private String zipCode;
}
