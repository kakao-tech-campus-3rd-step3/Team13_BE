package com.b4f2.pting.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.b4f2.pting.config.properties.KakaoOAuthProperties;
import com.b4f2.pting.dto.OAuthUrlResponse;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class AuthServiceTest {

    @Mock
    private KakaoOAuthProperties kakaoOAuthProperties;

    @InjectMocks
    private AuthService authService;

    @Test
    void getKakaoOAuthUrl_카카오OAuthURL조회_성공() {
        // given
        given(kakaoOAuthProperties.authUri()).willReturn("kakao_auth_uri");
        given(kakaoOAuthProperties.clientId()).willReturn("client_id");
        given(kakaoOAuthProperties.redirectUri()).willReturn("redirect_uri");

        // when
        OAuthUrlResponse urlResponse = authService.getKakaoOAuthUrl();

        // then
        assertThat(urlResponse)
                .isEqualTo(new OAuthUrlResponse(
                        "kakao_auth_uri?client_id=client_id&redirect_uri=redirect_uri&response_type=code"));
    }

    // TODO: 로그인 처리 메서드에 대한 테스트코드 작성하기!
}
