package com.ecommerce.userservice.service;

import com.ecommerce.userservice.dto.RegisterRequest;
import com.ecommerce.userservice.model.User;
import com.ecommerce.userservice.repository.UserRepository;
import com.ecommerce.userservice.security.JwtUtil;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository repository;
    private final PasswordEncoder encoder;
    private final JwtUtil jwtUtil;

    public String register(RegisterRequest registerRequest) {
        String email = registerRequest.getEmail();
        Optional<User> userOpt = repository.findByEmail(email);
        if (userOpt.isPresent()) {
            throw new IllegalArgumentException("Email already exists!");
        }
        String hashedPassword = encoder.encode(registerRequest.getPassword());
        User user = User.builder()
                .email(email)
                .firstName(registerRequest.getFirstName())
                .lastName(registerRequest.getLastName())
                .password(hashedPassword)
                .role(registerRequest.getRole())
                .phoneNumber(registerRequest.getPhoneNumber())
                .build();
        repository.save(user);
        return "User registered successfully";
    }

    public String login(String email, String password) {
        Optional<User> userOpt = repository.findByEmail(email);
        if (userOpt.isPresent() && encoder.matches(password, userOpt.get().getPassword())) {
            return jwtUtil.generateToken(
                    userOpt.get().getId(),
                    email,
                    userOpt.get().getFirstName(),
                    userOpt.get().getLastName(),
                    userOpt.get().getRole(),
                    userOpt.get().getPhoneNumber());
        }
        throw new IllegalArgumentException("Invalid credentials");
    }

    public User findById(Long id) {
        return repository.findById(id).orElseThrow(() ->
                new NoSuchElementException("User with id " + id + " does not exist!"));
    }

    public List<User> findAll(Integer page, Integer pageSize) {
        if (page == null && pageSize == null) {
            return repository.findAll();
        }
        if (page == null) {
            return repository.findAll(PageRequest.of(0, pageSize)).getContent();
        }
        if (pageSize == null) {
            return List.of();
        }
        return repository.findAll(PageRequest.of(page, pageSize)).getContent();
    }
}
