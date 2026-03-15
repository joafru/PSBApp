package com.boilerplate.aspect;

import com.boilerplate.annotation.ScopeAllowed;
import com.boilerplate.exception.ForbiddenException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

/**
 * AOP aspect that enforces {@link ScopeAllowed}.
 * Runs before every method annotated with @ScopeAllowed (or inside a
 * class annotated with @ScopeAllowed). The user must hold at least one
 * of the declared scopes, otherwise {@link ForbiddenException} is thrown.
 */
@Aspect
@Component
@Slf4j
public class ScopeAspect {

    @Before("@annotation(com.boilerplate.annotation.ScopeAllowed) || @within(com.boilerplate.annotation.ScopeAllowed)")
    public void validateScope(JoinPoint joinPoint) {
        ScopeAllowed scopeAllowed = resolveAnnotation(joinPoint);
        if (scopeAllowed == null) return;

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new ForbiddenException("Authentication required");
        }

        List<String> userScopes = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        String[] required = scopeAllowed.value();
        boolean hasScope = Arrays.stream(required).anyMatch(userScopes::contains);

        if (!hasScope) {
            log.warn("Access denied for '{}'. Required: {} | Has: {}",
                    auth.getName(), Arrays.toString(required), userScopes);
            throw new ForbiddenException(
                    "Insufficient scopes. Required one of: " + Arrays.toString(required));
        }
    }

    /** Method-level annotation takes priority over class-level. */
    private ScopeAllowed resolveAnnotation(JoinPoint joinPoint) {
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        ScopeAllowed ann = method.getAnnotation(ScopeAllowed.class);
        return ann != null ? ann : joinPoint.getTarget().getClass().getAnnotation(ScopeAllowed.class);
    }
}
