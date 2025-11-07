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
import io.swagger.v3.oas.models.tags.Tag;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(info())
                .servers(List.of(server()))
                .tags(tags())
                .components(components())
                .addSecurityItem(securityRequirement());
    }

    private Info info() {
        return new Info().title("P-Ting").description("API 명세").version("1.0.0");
    }

    private Server server() {
        return new Server().url("/api");
    }

    private List<Tag> tags() {
        return List.of(
                new Tag().name("OAuth 회원가입 API"),
                new Tag().name("각종 회원관련 기능 API"),
                new Tag().name("스포츠 목록 조회 API"),
                new Tag().name("게임 API"),
                new Tag().name("사용자 신고 API"),
                new Tag().name("FCM 푸쉬알림 토큰 API"),
                new Tag().name("알림 구독 관리 API"));
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
