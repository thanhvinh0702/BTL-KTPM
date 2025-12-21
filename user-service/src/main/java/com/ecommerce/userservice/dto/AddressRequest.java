package com.ecommerce.userservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AddressRequest {

    @NotNull(message = "street cannot be null")
    private String street;
    @NotNull(message = "zip code cannot be null")
    private String zipCode;
    @NotNull(message = "city cannot be null")
    private String city;
    @NotNull(message = "flat no cannot be null")
    private String flatNo;
    @NotNull(message = "state cannot be null")
    private String state;
}
