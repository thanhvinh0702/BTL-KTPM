package com.ecommerce.cartservice.security;

import com.ecommerce.cartservice.exception.ForbiddenException;
import com.ecommerce.cartservice.exception.UnauthorizedException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.CodeSignature;
import org.springframework.stereotype.Component;

@Component
@Aspect
@Slf4j
public class RequireOwnerAspect {
    @Before("@annotation(requireOwner)")
    public void checkOwner(JoinPoint joinPoint, RequireOwner requireOwner) {
        String userIdContext = UserContext.getUserId();

        if (userIdContext == null) {
            throw new UnauthorizedException("User not authenticated");
        }

        // name of the parameter that stores userId
        String paramName = requireOwner.value();

        // read method parameter names and values
        CodeSignature signature = (CodeSignature) joinPoint.getSignature();
        String[] paramNames = signature.getParameterNames();
        Object[] paramValues = joinPoint.getArgs();

        Object targetUserId = null;

        // find parameter named "userId"
        for (int i = 0; i < paramNames.length; i++) {
            if (paramNames[i].equals(paramName)) {
                targetUserId = paramValues[i];
                break;
            }
        }

        if (targetUserId == null) {
            throw new ForbiddenException("Missing userId parameter for owner validation");
        }

        // Compare userId
        if (!userIdContext.equals(String.valueOf(targetUserId))) {
            log.warn("Forbidden access: user={} tried to access resource of user={}",
                    userIdContext, targetUserId);
            throw new ForbiddenException("You are not allowed to access this resource");
        }
    }
}
