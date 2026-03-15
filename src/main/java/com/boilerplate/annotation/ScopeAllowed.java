package com.boilerplate.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Restricts a controller method (or whole controller) to users that hold
 * AT LEAST ONE of the listed scopes.
 *
 * <pre>
 * {@literal @}ScopeAllowed({"user.read"})
 * {@literal @}GetMapping("/users")
 * public ResponseEntity<...> listUsers() { ... }
 * </pre>
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ScopeAllowed {
    String[] value();
}
