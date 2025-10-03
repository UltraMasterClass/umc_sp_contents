package com.umc.sp.contents.exception;

/**
 * Exception thrown when authentication fails
 * This includes invalid tokens, expired tokens, missing tokens, etc.
 */
public class AuthenticationException extends RuntimeException {
    
    public AuthenticationException(String message) {
        super(message);
    }
    
    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
    
    /**
     * Create a user-friendly authentication exception based on the original cause
     */
    public static AuthenticationException fromSecurityException(org.springframework.security.core.AuthenticationException securityException) {
        String message = securityException.getMessage();
        
        if (message != null) {
            if (message.contains("JWT expired")) {
                return new AuthenticationException("Authentication token has expired. Please login again.");
            } else if (message.contains("JWT signature")) {
                return new AuthenticationException("Invalid authentication token signature.");
            } else if (message.contains("JWT claims")) {
                return new AuthenticationException("Invalid authentication token claims.");
            } else if (message.contains("Bearer")) {
                return new AuthenticationException("Invalid or missing Bearer token in Authorization header.");
            }
        }
        
        return new AuthenticationException("Authentication failed. Please provide a valid token.");
    }
}
