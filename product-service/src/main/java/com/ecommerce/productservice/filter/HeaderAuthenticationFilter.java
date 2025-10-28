package com.ecommerce.productservice.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.util.Collections;
import java.util.List;

@Component
public class HeaderAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws java.io.IOException, jakarta.servlet.ServletException {

        String userIdHeader = request.getHeader("x-user-id");
        String roleHeader = request.getHeader("x-user-role");

        if (userIdHeader != null && roleHeader != null) {
            Long userId = Long.parseLong(userIdHeader);
            List<String> roles = Collections.singletonList(roleHeader.toUpperCase());
            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(new AuthUser(userId, roles), null, Collections.emptyList());
            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        filterChain.doFilter(request, response);
    }

    // Simple principal class to hold ID + roles
    public static class AuthUser {
        private Long id;
        private List<String> roles;

        public AuthUser(Long id, List<String> roles) {
            this.id = id;
            this.roles = roles;
        }

        public Long getId() { return id; }
        public List<String> getRoles() { return roles; }
    }
}
