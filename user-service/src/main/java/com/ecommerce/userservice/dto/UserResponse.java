package com.ecommerce.userservice.dto;

import com.ecommerce.userservice.mapper.AddressMapper;
import com.ecommerce.userservice.model.Address;
import com.ecommerce.userservice.model.Role;
import com.ecommerce.userservice.model.User;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder
public class UserResponse {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private Role role;
    private String phoneNumber;
    private AddressResponse address;

}
