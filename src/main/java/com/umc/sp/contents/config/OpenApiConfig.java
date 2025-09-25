package com.umc.sp.contents.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Swagger/OpenAPI 3 Configuration for Contents API
 * 
 * Minimal configuration focused on maintainability and performance.
 * Uses SpringDoc auto-detection for schemas and responses.
 */
@Configuration
public class OpenApiConfig {

    @Value("${server.port:8082}")
    private String serverPort;

    /**
     * Basic OpenAPI configuration for Contents API
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("UMC Streaming Platform - Contents API")
                        .description("RESTful APIs for content management and discovery operations. " +
                                   "Features comprehensive content catalog management, search capabilities, " +
                                   "subscription plans, and content categorization for the UMC Streaming Platform.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("UltraMaster Class Dev Team")
                                .email("admontech@ultramasterclass.com")
                        )
                )
                .servers(List.of(
                        new Server()
                                .url("http://localhost:" + serverPort)
                                .description("Local Development"),
                        new Server()
                                .url("https://api-sp-contents-dev.ultramasterclass.com")
                                .description("Development Environment"),
                        new Server()
                                .url("https://api-sp-contents-staging.ultramasterclass.com")
                                .description("Staging Environment"),
                        new Server()
                                .url("https://api.ultramasterclass.com")
                                .description("Production Environment")
                ));
    }
}
