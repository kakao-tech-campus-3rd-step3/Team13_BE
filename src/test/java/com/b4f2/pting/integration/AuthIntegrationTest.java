package com.b4f2.pting.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.b4f2.pting.config.TestContainersConfig;
import com.b4f2.pting.domain.Member;
import com.b4f2.pting.domain.Member.OAuthProvider;
import com.b4f2.pting.dto.AuthResponse;
import com.b4f2.pting.dto.KakaoOAuthTokenResponse;
import com.b4f2.pting.dto.KakaoUserInfoResponse;
import com.b4f2.pting.repository.MemberRepository;
import com.b4f2.pting.util.KakaoOAuthClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@Import(TestContainersConfig.class)
class AuthIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @MockitoBean
    private KakaoOAuthClient kakaoOAuthClient; // 외부 API 호출 Mocking

    @Autowired
    private MemberRepository memberRepository;

    @BeforeEach
    void setUp() {
        memberRepository.deleteAll();
    }

    @Test
    @DisplayName("카카오 신규 회원 로그인")
    void KakaoLogin_신규회원_성공() {
        // given
        String code = "testCode";

        KakaoOAuthTokenResponse tokenResponse = new KakaoOAuthTokenResponse(
            "bearer",
            "accessToken123",
            3600,
            "refreshToken123",
            7200
        );

        KakaoUserInfoResponse userInfoResponse = new KakaoUserInfoResponse(12345L);

        given(kakaoOAuthClient.getKakaoOAuthToken(code)).willReturn(tokenResponse);
        given(kakaoOAuthClient.getKakaoUserInfo(tokenResponse)).willReturn(userInfoResponse);

        // when
        ResponseEntity<AuthResponse> response = restTemplate.getForEntity(
            "/api/v1/auth/kakao/callback?code=" + code,
            AuthResponse.class
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().token()).isNotBlank();

        Member member = memberRepository.findByOauthIdAndOauthProvider(12345L, OAuthProvider.KAKAO).orElseThrow();
        assertThat(member.getOauthId()).isEqualTo(12345L);
    }

    @Test
    @DisplayName("카카오 기존 회원 로그인")
    void KakaoLogin_기존회원_성공() {
        // given
        Member existingMember = memberRepository.save(new Member(12345L, OAuthProvider.KAKAO));

        String code = "testCode";
        KakaoOAuthTokenResponse tokenResponse = new KakaoOAuthTokenResponse(
            "bearer",
            "accessToken123",
            3600,
            "refreshToken123",
            7200
        );

        KakaoUserInfoResponse userInfoResponse = new KakaoUserInfoResponse(12345L);

        given(kakaoOAuthClient.getKakaoOAuthToken(code)).willReturn(tokenResponse);
        given(kakaoOAuthClient.getKakaoUserInfo(tokenResponse)).willReturn(userInfoResponse);

        // when
        ResponseEntity<AuthResponse> response = restTemplate.getForEntity(
            "/api/v1/auth/kakao/callback?code=" + code,
            AuthResponse.class
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(memberRepository.findAll()).hasSize(1);
    }
}

