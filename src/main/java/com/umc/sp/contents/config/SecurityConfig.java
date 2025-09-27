package com.umc.sp.contents.config;

import com.umc.sp.contents.config.properties.CorsProperties;
import com.umc.sp.contents.config.properties.SecurityProperties;
import com.umc.sp.contents.security.converter.CognitoJwtAuthenticationConverter;
import com.umc.sp.contents.security.filter.CustomJwtAuthenticationFilter;
import com.umc.sp.contents.security.handler.DelegatingAuthenticationEntryPoint;
import com.umc.sp.contents.security.validator.CognitoJwtValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.ArrayList;
import java.util.List;

/**
 * Security configuration for UMC Contents Microservice
 * Supports OAuth2 Resource Server with AWS Cognito
 * Uses feature flag to enable/disable security
 */
@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final SecurityProperties securityProperties;
    private final CorsProperties corsProperties;
    private final CustomJwtAuthenticationFilter customJwtAuthenticationFilter;
    private final DelegatingAuthenticationEntryPoint authenticationEntryPoint;
    private final CognitoJwtAuthenticationConverter cognitoJwtAuthenticationConverter;
    private final CognitoJwtValidator cognitoJwtValidator;
    
    @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}")
    private String jwkSetUri;

    /**
     * Security filter chain when security is DISABLED
     * All endpoints are accessible without authentication
     */
    @Bean
    @ConditionalOnProperty(name = "umc.security.enabled", havingValue = "false", matchIfMissing = true)
    public SecurityFilterChain disabledSecurityFilterChain(HttpSecurity http) throws Exception {
        log.warn("⚠️  Security is DISABLED - All endpoints are publicly accessible");
        
        http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .authorizeHttpRequests(authorize -> authorize
                .anyRequest().permitAll()
            )
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }

    /**
     * Security filter chain when security is ENABLED
     * Validates JWT tokens from AWS Cognito
     */
    @Bean
    @ConditionalOnProperty(name = "umc.security.enabled", havingValue = "true")
    public SecurityFilterChain enabledSecurityFilterChain(HttpSecurity http) throws Exception {
        log.info("🔒 Security is ENABLED - JWT validation active");
        log.info("📝 Public endpoints: {}", securityProperties.getPublicEndpoints());
        log.info("🔐 Protected endpoints: {}", securityProperties.getProtectedEndpoints());
        
        http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(authorize -> authorize
                // Public endpoints
                .requestMatchers(securityProperties.getPublicEndpoints().toArray(new String[0])).permitAll()
                // All other requests require authentication
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt
                    .decoder(jwtDecoder())
                    .jwtAuthenticationConverter(cognitoJwtAuthenticationConverter)
                )
                .authenticationEntryPoint(authenticationEntryPoint)
            )
            .exceptionHandling(exceptions -> exceptions
                .authenticationEntryPoint(authenticationEntryPoint)
            )
            // Add custom filter for development mode support
            .addFilterBefore(customJwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * CORS configuration
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(corsProperties.getAllowedOrigins());
        configuration.setAllowedMethods(corsProperties.getAllowedMethods());
        configuration.setAllowedHeaders(corsProperties.getAllowedHeaders());
        configuration.setAllowCredentials(corsProperties.isAllowCredentials());
        configuration.setMaxAge(corsProperties.getMaxAge());
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
    
    /**
     * Custom JWT decoder with Cognito-specific validators
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
        
        return jwtDecoder;
    }
}
