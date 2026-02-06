package com.ecommerce.userservice.controller;

import com.ecommerce.userservice.dto.LoginResponse;
import com.ecommerce.userservice.dto.RegisterRequest;
import com.ecommerce.userservice.service.UserService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/users/auth")
@AllArgsConstructor
public class AuthController {
    private final UserService service;
    @GetMapping("/ping")
    public ResponseEntity<String> ping(){
        return new ResponseEntity<String>("Ping",HttpStatus.OK );
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest) {
        return new ResponseEntity<>(service.register(registerRequest), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody Map<String, String> body) {
        LoginResponse loginResponse = service.login(body.get("email"), body.get("password"));
        return new ResponseEntity<>(loginResponse, HttpStatus.OK);
    }
}
