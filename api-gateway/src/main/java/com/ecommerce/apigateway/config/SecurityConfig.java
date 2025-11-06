package com.ecommerce.apigateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
public class SecurityConfig {

//    @Bean
//    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
//        http
//                .csrf(ServerHttpSecurity.CsrfSpec::disable) // Tắt CSRF cho REST API
//                .authorizeExchange(exchange -> exchange
//                        .anyExchange().permitAll() // Cho phép tất cả request (không cần login)
//                )
//                .formLogin(ServerHttpSecurity.FormLoginSpec::disable) // Tắt form login
//                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable); // Tắt HTTP Basic
//
//        return http.build();
//    }
}
