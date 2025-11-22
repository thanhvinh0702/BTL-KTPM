package com.ecommerce.cartservice.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class UserContextInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {
        String userId = request.getHeader("X-USER-ID");
        String role = request.getHeader("X-USER_ROLE");

        if (userId == null) {

        }

        UserContext.setUserId(userId);
        UserContext.setRole(role);

        return true;
    }
}
