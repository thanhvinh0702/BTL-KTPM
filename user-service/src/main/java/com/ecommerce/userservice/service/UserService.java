package com.ecommerce.userservice.service;

import com.ecommerce.userservice.dto.LoginResponse;
import com.ecommerce.userservice.dto.RegisterRequest;
import com.ecommerce.userservice.dto.UserResponse;
import com.ecommerce.userservice.mapper.UserMapper;
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
    private final UserMapper userMapper;

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

    public LoginResponse login(String email, String password) {
        Optional<User> userOpt = repository.findByEmail(email);
        if (userOpt.isPresent() && encoder.matches(password, userOpt.get().getPassword())) {
            return new LoginResponse(jwtUtil.generateToken(
                    userOpt.get().getId(),
                    email,
                    userOpt.get().getFirstName(),
                    userOpt.get().getLastName(),
                    userOpt.get().getRole(),
                    userOpt.get().getPhoneNumber()), userOpt.get().getId());
        }
        throw new IllegalArgumentException("Invalid credentials");
    }

    public UserResponse findById(Long id) {
        return userMapper.toResponse(repository.findById(id).orElseThrow(() ->
                new NoSuchElementException("User with id " + id + " does not exist!")));
    }

    public List<UserResponse> findAll(Integer page, Integer pageSize) {
        if (page == null && pageSize == null) {
            return repository.findAll()
                    .stream()
                    .map(userMapper::toResponse)
                    .toList();
        }
        if (page == null) {
            return repository.findAll(PageRequest.of(0, pageSize)).getContent()
                    .stream()
                    .map(userMapper::toResponse)
                    .toList();
        }
        if (pageSize == null) {
            return List.of();
        }
        return repository.findAll(PageRequest.of(page, pageSize)).getContent()
                .stream()
                .map(userMapper::toResponse)
                .toList();
    }
}
