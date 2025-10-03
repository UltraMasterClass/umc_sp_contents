package com.umc.sp.contents.security.converter;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Custom JWT converter for AWS Cognito tokens
 * Handles Cognito-specific claims and converts them to Spring Security authorities
 */
@Slf4j
@Component
public class CognitoJwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {
    
    private final JwtGrantedAuthoritiesConverter defaultGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
    
    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        log.debug("Converting JWT to Authentication token");
        log.debug("JWT Subject: {}", jwt.getSubject());
        log.debug("JWT Claims: {}", jwt.getClaims().keySet());
        
        Collection<GrantedAuthority> authorities = extractAuthorities(jwt);
        
        return new JwtAuthenticationToken(jwt, authorities);
    }
    
    private Collection<GrantedAuthority> extractAuthorities(Jwt jwt) {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        
        // Extract default authorities (from scope claim)
        authorities.addAll(defaultGrantedAuthoritiesConverter.convert(jwt));
        
        // Extract Cognito groups as authorities
        List<String> cognitoGroups = jwt.getClaimAsStringList("cognito:groups");
        if (CollectionUtils.isNotEmpty(cognitoGroups)) {
            log.debug("Found Cognito groups: {}", cognitoGroups);
            authorities.addAll(
                cognitoGroups.stream()
                    .map(group -> new SimpleGrantedAuthority("ROLE_" + group))
                    .collect(Collectors.toList())
            );
        }
        
        log.debug("Total authorities extracted: {}", authorities.size());
        authorities.forEach(auth -> log.debug("Authority: {}", auth.getAuthority()));
        
        return authorities;
    }
}
