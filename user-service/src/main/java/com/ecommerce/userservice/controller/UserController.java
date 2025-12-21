package com.ecommerce.userservice.controller;

import com.ecommerce.userservice.dto.AddressRequest;
import com.ecommerce.userservice.dto.AddressResponse;
import com.ecommerce.userservice.dto.UserResponse;
import com.ecommerce.userservice.service.AddressService;
import com.ecommerce.userservice.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@AllArgsConstructor
public class UserController {

    private final UserService userService;
    private final AddressService addressService;

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> findById(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.findById(userId));
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> findAll(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer pageSize
    ) {
        return ResponseEntity.ok(userService.findAll(page, pageSize));
    }

    @PostMapping("/{userId}/address")
    public ResponseEntity<AddressResponse> createAddress(@PathVariable Long userId,
                                                         @RequestBody @Validated AddressRequest addressRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!authentication.getName().equals(userId.toString())) {
            throw new AccessDeniedException("Forbidden");
        }
        return ResponseEntity.ok(addressService.createOrUpdateAddress(userId, addressRequest));
    }
}
