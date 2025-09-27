package com.umc.sp.contents.security.validator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Custom JWT validator for AWS Cognito tokens
 * Validates the audience claim using client_id instead of aud
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CognitoJwtValidator implements OAuth2TokenValidator<Jwt> {
    
    @Value("${spring.security.oauth2.resourceserver.jwt.audiences}")
    private String expectedAudience;
    
    @Override
    public OAuth2TokenValidatorResult validate(Jwt jwt) {
        log.debug("Validating JWT token");
        log.debug("Expected audience: {}", expectedAudience);
        
        // Cognito uses 'client_id' instead of 'aud' for audience
        String actualAudience = jwt.getClaimAsString("client_id");
        if (actualAudience == null) {
            // Fallback to standard 'aud' claim
            actualAudience = jwt.getClaimAsString("aud");
        }
        
        log.debug("Actual audience (client_id or aud): {}", actualAudience);
        
        if (StringUtils.hasText(actualAudience) && actualAudience.equals(expectedAudience)) {
            log.debug("Token validation successful - audience matches");
            return OAuth2TokenValidatorResult.success();
        }
        
        log.warn("Token validation failed - audience mismatch. Expected: {}, Actual: {}", 
                expectedAudience, actualAudience);
        
        OAuth2Error error = new OAuth2Error(
                "invalid_audience", 
                "The required audience is missing or doesn't match", 
                null
        );
        
        return OAuth2TokenValidatorResult.failure(error);
    }
}
