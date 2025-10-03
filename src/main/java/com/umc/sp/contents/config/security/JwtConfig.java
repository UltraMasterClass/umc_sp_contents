package com.umc.sp.contents.config.security;

import com.umc.sp.contents.security.validator.CognitoJwtValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.*;

import java.util.ArrayList;
import java.util.List;

/**
 * JWT Configuration
 * Handles JWT decoder and validators for AWS Cognito
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class JwtConfig {
    
    private final CognitoJwtValidator cognitoJwtValidator;
    
    @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}")
    private String jwkSetUri;

    /**
     * Custom JWT decoder with Cognito-specific validators
     * Only created when security is enabled
     */
    @Bean
    @ConditionalOnProperty(name = "umc.security.enabled", havingValue = "true")
    public JwtDecoder jwtDecoder() {
        log.info("🔑 Configuring JWT decoder for Cognito with JWK Set URI: {}", jwkSetUri);
        
        NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build();
        
        // Add custom validators
        List<OAuth2TokenValidator<Jwt>> validators = new ArrayList<>();
        validators.add(new JwtTimestampValidator());
        validators.add(cognitoJwtValidator);
        
        OAuth2TokenValidator<Jwt> combinedValidator = new DelegatingOAuth2TokenValidator<>(validators);
        jwtDecoder.setJwtValidator(combinedValidator);
        
        log.debug("JWT decoder configured with {} validators", validators.size());
        
        return jwtDecoder;
    }
}
