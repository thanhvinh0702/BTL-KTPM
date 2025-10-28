package com.ecommerce.userservice.dto;

import com.ecommerce.userservice.model.Role;
import lombok.Data;

@Data
public class RegisterRequest {
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private Role role;
    private String phoneNumber;
}
