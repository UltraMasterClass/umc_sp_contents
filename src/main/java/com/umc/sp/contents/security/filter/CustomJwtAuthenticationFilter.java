package com.umc.sp.contents.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.umc.sp.contents.config.properties.SecurityProperties;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Custom JWT authentication filter for development support
 * Allows using fixed development tokens when in development mode
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CustomJwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    
    private final SecurityProperties securityProperties;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                  HttpServletResponse response, 
                                  FilterChain filterChain) throws ServletException, IOException {
        
        // Skip filter if security is disabled
        if (!securityProperties.isEnabled()) {
            filterChain.doFilter(request, response);
            return;
        }

        // Check if this is a public endpoint
        String requestPath = request.getRequestURI();
        if (isPublicEndpoint(requestPath)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String token = extractToken(request);
            
            // Development mode support
            if (securityProperties.isDevelopmentMode() && token != null) {
                if (token.equals(securityProperties.getDevelopmentToken())) {
                    log.debug("🔧 Development token validated for path: {}", requestPath);
                    setDevelopmentAuthentication();
                    filterChain.doFilter(request, response);
                    return;
                }
            }
            
            // In production mode, JWT validation is handled by Spring Security OAuth2 Resource Server
            // This filter just adds additional logging and error handling
            if (token == null) {
                log.warn("🚫 No token provided for protected endpoint: {}", requestPath);
                sendErrorResponse(response, HttpStatus.UNAUTHORIZED, "No authentication token provided");
                return;
            }

            // Continue with the filter chain - JWT validation will be done by OAuth2 Resource Server
            filterChain.doFilter(request, response);
            
        } catch (Exception e) {
            log.error("❌ Authentication error: {}", e.getMessage(), e);
            sendErrorResponse(response, HttpStatus.UNAUTHORIZED, "Authentication failed: " + e.getMessage());
        }
    }

    /**
     * Extract token from Authorization header
     */
    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length());
        }
        return null;
    }

    /**
     * Check if the request path is a public endpoint
     */
    private boolean isPublicEndpoint(String path) {
        return securityProperties.getPublicEndpoints().stream()
                .anyMatch(publicPath -> {
                    if (publicPath.endsWith("/**")) {
                        String basePath = publicPath.substring(0, publicPath.length() - 3);
                        return path.startsWith(basePath);
                    }
                    return path.equals(publicPath);
                });
    }

    /**
     * Set authentication context for development mode
     */
    private void setDevelopmentAuthentication() {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                "dev-user",
                null,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_DEVELOPER"))
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    /**
     * Send JSON error response
     */
    private void sendErrorResponse(HttpServletResponse response, HttpStatus status, String message) throws IOException {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now().toString());
        errorResponse.put("status", status.value());
        /*errorResponse.put("error", status.getReasonPhrase());
        errorResponse.put("message", message);
        errorResponse.put("path", SecurityContextHolder.getContext().getAuthentication() != null ? 
                SecurityContextHolder.getContext().getAuthentication().getName() : "");*/
        
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        // Don't filter if security is disabled
        return !securityProperties.isEnabled();
    }
}
