package com.ecommerce.apigateway.config; // Sửa lại package cho đúng với project bạn

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        return http
                // Tắt CSRF vì chúng ta dùng JWT (stateless)
                .csrf(ServerHttpSecurity.CsrfSpec::disable)

                // Cấu hình CORS (Quan trọng để trình duyệt gọi được)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // Cho phép truy cập
                .authorizeExchange(exchange -> exchange
                        // Cho phép API đăng ký/đăng nhập (Auth)
                        .pathMatchers("/api/v1/users/auth/**").permitAll()
                        // Quan trọng: Cho phép phương thức OPTIONS (Pre-flight request của trình duyệt)
                        .pathMatchers(org.springframework.http.HttpMethod.OPTIONS).permitAll()
                        // Cho phép TẤT CẢ request khác đi qua (vì JwtAuthFilter sẽ chặn sau)
                        .anyExchange().permitAll()
                )
                .build();
    }

    // Cấu hình CORS chi tiết cho Spring Security
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:3000")); // Frontend của bạn
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}