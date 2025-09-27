package com.umc.sp.contents.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.umc.sp.contents.exception.AuthenticationException;
import com.umc.sp.contents.exception.ErrorResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Authentication entry point that delegates to our existing exception handling system
 * This is required by Spring Security to handle authentication errors at the filter level
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DelegatingAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         org.springframework.security.core.AuthenticationException authException) 
                         throws IOException, ServletException {
        
        log.warn("Authentication failed at filter level: {} - Path: {}", 
                authException.getMessage(), request.getRequestURI());
        
        // Convert Spring Security exception to our custom exception
        AuthenticationException customException = AuthenticationException.fromSecurityException(authException);
        
        // Send response using the same format as our CustomExceptionHandler
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        
        ErrorResponse errorResponse = new ErrorResponse(customException.getMessage());
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
