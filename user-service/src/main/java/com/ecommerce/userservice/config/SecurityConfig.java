package com.ecommerce.userservice.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/swagger-ui/**").permitAll()
                        .requestMatchers("/v3/api-docs/**").permitAll()
                        .requestMatchers("/swagger-resources/**").permitAll()
                        .requestMatchers("/webjars/**").permitAll()
                        .requestMatchers("/api/v1/users/auth/**").permitAll()
                        .anyRequest().authenticated())
                .addFilterBefore(headerFilterAuth(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public OncePerRequestFilter headerFilterAuth() {
        return new OncePerRequestFilter() {

            private final List<String> publicPaths = List.of(
                    "/swagger-ui",
                    "/v3/api-docs",
                    "/swagger-resources",
                    "/webjars",
                    "/api/v1/users/auth");

            @Override
            protected void doFilterInternal(HttpServletRequest request,
                    HttpServletResponse response,
                    FilterChain filterChain) throws ServletException, IOException {
                String path = request.getRequestURI();
                if (publicPaths.stream().anyMatch(path::startsWith)) {
                    filterChain.doFilter(request, response);
                    return;
                }
                String role = request.getHeader("X-USER-ROLE");
                String userId = request.getHeader("X-USER-ID");
                if (role == null || role.isBlank() || userId == null || userId.isBlank()) {
                    filterChain.doFilter(request, response);
                    return;
                }
                String roleName = role.startsWith("ROLE_") ? role : "ROLE_" + role;
                GrantedAuthority authority = new SimpleGrantedAuthority(roleName);
                Authentication authentication = new UsernamePasswordAuthenticationToken(
                        userId,
                        null,
                        List.of(authority));
                SecurityContextHolder.getContext().setAuthentication(authentication);
                filterChain.doFilter(request, response);
            }
        };
    }
}
