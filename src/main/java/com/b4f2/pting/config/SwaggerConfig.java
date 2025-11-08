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
        return new Info().title("P-Ting").version("1.0.0");
    }

    private Server server() {
        return new Server().url("/");
    }

    private List<Tag> tags() {
        return List.of(
                new Tag().name("회원가입 및 로그인 API").description("카카오, 구글 OAuth를 통해 회원가입 및 로그인"),
                new Tag().name("각종 회원관련 기능 API").description("학교 선택, 이메일 인증, 프로필 조회, 프로필 정보 수정, 내 게임 조회"),
                new Tag().name("스포츠 목록 조회 API"),
                new Tag().name("게임 API").description("전체 게임 조회, 게임 생성, 게임 상세정보 조회, 게임 참가, 게임 나가기, 승리 팀 투표 (게임 종료 후)"),
                new Tag().name("랭크게임 API").description("랭크매칭 대기열 등록, 매칭된 게임 참가"),
                new Tag().name("사용자 신고 API").description("사용자 신고 (게임 종료 후)"),
                new Tag().name("FCM 푸쉬알림 토큰 API").description("유저의 FCM 토큰 등록 및 삭제"),
                new Tag().name("알림 구독 관리 API").description("시간대별, 스포츠 종류별 구독 등록 및 삭제"));
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
