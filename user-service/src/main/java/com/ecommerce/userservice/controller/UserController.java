package com.ecommerce.userservice.controller;

import com.ecommerce.userservice.dto.UserResponse;
import com.ecommerce.userservice.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> findById(@PathVariable Long userId) {
        return ResponseEntity.ok(UserResponse.mapUserToUserResponse(userService.findById(userId)));
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> findAll(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer pageSize
    ) {
        return ResponseEntity.ok(userService.findAll(page, pageSize)
                .stream()
                .map(UserResponse::mapUserToUserResponse)
                .toList());
    }
}
