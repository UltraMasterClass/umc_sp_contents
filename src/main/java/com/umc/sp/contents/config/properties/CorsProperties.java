package com.umc.sp.contents.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * CORS configuration properties for UMC Contents Microservice
 * Allows flexible configuration of CORS settings via application properties
 */
@Data
@Component
@ConfigurationProperties(prefix = "umc.cors")
public class CorsProperties {
    
    /**
     * List of allowed origins for CORS
     * Use "*" to allow all origins (not recommended for production)
     */
    private List<String> allowedOrigins = List.of("*");
    
    /**
     * List of allowed HTTP methods
     */
    private List<String> allowedMethods = List.of("GET", "POST", "PUT", "DELETE", "OPTIONS");
    
    /**
     * List of allowed headers
     */
    private List<String> allowedHeaders = List.of("*");
    
    /**
     * Whether credentials are allowed in CORS requests
     */
    private boolean allowCredentials = true;
    
    /**
     * Max age for preflight requests caching (in seconds)
     */
    private long maxAge = 3600;
}
