package com.umc.sp.contents.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Security configuration properties for UMC Contents Microservice
 * Allows flexible configuration of security features via application properties
 */
@Data
@Component
@ConfigurationProperties(prefix = "umc.security")
public class SecurityProperties {
    
    /**
     * Feature flag to enable/disable security
     * When false, all endpoints are accessible without authentication
     */
    private boolean enabled = false;
    
    /**
     * Development mode flag
     * When true, allows fixed development tokens for testing
     */
    private boolean developmentMode = false;
    
    /**
     * Fixed token for development mode
     * Only used when developmentMode is true
     */
    private String developmentToken = "dev-token-123";
    
    /**
     * List of public endpoints that don't require authentication
     * Even when security is enabled, these endpoints remain public
     */
    private List<String> publicEndpoints = List.of(
        "/actuator/health",
        "/actuator/health/**",
        "/actuator/info",
        "/swagger-ui.html",
        "/swagger-ui/**",
        "/v3/api-docs/**",
        "/webjars/**",
        "/favicon.ico"
    );
    
    /**
     * List of endpoints that require authentication
     * Used when security is enabled
     */
    private List<String> protectedEndpoints = List.of(
        "/content/**",
        "/sections/**",
        "/internal/**"
    );
}
