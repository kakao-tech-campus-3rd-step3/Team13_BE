package com.b4f2.pting.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(info())
                .servers(List.of(server()))
                .components(components())
                .addSecurityItem(securityRequirement());
    }

    private Info info() {
        return new Info().title("P-Ting").description("API 명세").version("1.0.0");
    }

    private Server server() {
        return new Server().url("/api");
    }

    private Components components() {
        return new Components()
                .addSecuritySchemes(
                        "JWT",
                        new SecurityScheme()
                                .name("JWT")
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT"));
    }

    private SecurityRequirement securityRequirement() {
        return new SecurityRequirement().addList("JWT");
    }
}
