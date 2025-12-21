package com.ecommerce.userservice.mapper;

import com.ecommerce.userservice.dto.AddressResponse;
import com.ecommerce.userservice.model.Address;
import org.springframework.stereotype.Component;

@Component
public class AddressMapper {

    public AddressResponse toResponse(Address address) {
        return AddressResponse.builder()
                .street(address.getStreet())
                .city(address.getCity())
                .zipCode(address.getZipCode())
                .flatNo(address.getFlatNo())
                .state(address.getState())
                .build();
    }
}
