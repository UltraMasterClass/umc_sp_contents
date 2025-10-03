package com.umc.sp.contents.config.security;

import com.umc.sp.contents.config.properties.SecurityProperties;
import com.umc.sp.contents.security.converter.CognitoJwtAuthenticationConverter;
import com.umc.sp.contents.security.filter.CustomJwtAuthenticationFilter;
import com.umc.sp.contents.security.handler.DelegatingAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

/**
 * Security configuration when security is ENABLED
 * Validates JWT tokens from AWS Cognito
 */
@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@ConditionalOnProperty(name = "umc.security.enabled", havingValue = "true")
public class EnabledSecurityConfig {

    private final SecurityProperties securityProperties;
    private final CustomJwtAuthenticationFilter customJwtAuthenticationFilter;
    private final DelegatingAuthenticationEntryPoint authenticationEntryPoint;
    private final CognitoJwtAuthenticationConverter cognitoJwtAuthenticationConverter;
    private final CorsConfigurationSource corsConfigurationSource;
    private final JwtDecoder jwtDecoder;

    @Bean
    public SecurityFilterChain enabledSecurityFilterChain(HttpSecurity http) throws Exception {
        log.info("🔒 Security is ENABLED - JWT validation active");
        log.info("📝 Public endpoints: {}", securityProperties.getPublicEndpoints());
        log.info("🔐 Protected endpoints: {}", securityProperties.getProtectedEndpoints());
        
        http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(cors -> cors.configurationSource(corsConfigurationSource))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(authorize -> authorize
                // Public endpoints
                .requestMatchers(securityProperties.getPublicEndpoints().toArray(new String[0])).permitAll()
                // All other requests require authentication
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt
                    .decoder(jwtDecoder)
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
}
