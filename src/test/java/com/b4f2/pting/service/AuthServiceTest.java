package com.b4f2.pting.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.b4f2.pting.config.properties.KakaoOAuthProperties;
import com.b4f2.pting.domain.Member;
import com.b4f2.pting.domain.MemberStatus;
import com.b4f2.pting.dto.AuthResponse;
import com.b4f2.pting.dto.KakaoOAuthTokenResponse;
import com.b4f2.pting.dto.KakaoUserInfoResponse;
import com.b4f2.pting.dto.OAuthUrlResponse;
import com.b4f2.pting.repository.MemberRepository;
import com.b4f2.pting.util.JwtUtil;
import com.b4f2.pting.util.KakaoOAuthClient;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class AuthServiceTest {

    @Mock
    private KakaoOAuthProperties kakaoOAuthProperties;

    @Mock
    private KakaoOAuthClient kakaoOAuthClient;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    private Member activeMember;
    private Member suspendedMember;
    private Member bannedMember;

    @BeforeEach
    void setUp() {
        activeMember = new Member("activeId", Member.OAuthProvider.KAKAO);
        suspendedMember = new Member("suspendedId", Member.OAuthProvider.KAKAO);
        suspendedMember.changeStatus(MemberStatus.SUSPENDED);
        bannedMember = new Member("bannedId", Member.OAuthProvider.KAKAO);
        bannedMember.changeStatus(MemberStatus.BANNED);
    }

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

    @Test
    void activeMember_로그인_성공() {
        // given
        KakaoOAuthTokenResponse tokenResponse = mock(KakaoOAuthTokenResponse.class);
        KakaoUserInfoResponse userInfo = new KakaoUserInfoResponse(activeMember.getOauthId());

        given(kakaoOAuthClient.getKakaoOAuthToken(anyString())).willReturn(tokenResponse);
        given(kakaoOAuthClient.getKakaoUserInfo(tokenResponse)).willReturn(userInfo);
        given(memberRepository.findByOauthIdAndOauthProvider(activeMember.getOauthId(), Member.OAuthProvider.KAKAO))
                .willReturn(Optional.of(activeMember));
        given(jwtUtil.createToken(activeMember)).willReturn("jwtToken");

        // when
        AuthResponse response = authService.kakaoOAuthLogin("dummyCode");

        // then
        assertThat(response.token()).isEqualTo("jwtToken");
    }

    @Test
    void suspendedMember_로그인_가능() {
        // given
        KakaoOAuthTokenResponse tokenResponse = mock(KakaoOAuthTokenResponse.class);
        KakaoUserInfoResponse userInfo = new KakaoUserInfoResponse(suspendedMember.getOauthId());

        given(kakaoOAuthClient.getKakaoOAuthToken(anyString())).willReturn(tokenResponse);
        given(kakaoOAuthClient.getKakaoUserInfo(tokenResponse)).willReturn(userInfo);
        given(memberRepository.findByOauthIdAndOauthProvider(suspendedMember.getOauthId(), Member.OAuthProvider.KAKAO))
                .willReturn(Optional.of(suspendedMember));
        given(jwtUtil.createToken(suspendedMember)).willReturn("jwtToken");

        // when
        AuthResponse response = authService.kakaoOAuthLogin("dummyCode");

        // then
        assertThat(response.token()).isEqualTo("jwtToken");
    }

    @Test
    void bannedMember_로그인_불가() {
        // given
        KakaoOAuthTokenResponse tokenResponse = mock(KakaoOAuthTokenResponse.class);
        KakaoUserInfoResponse userInfo = new KakaoUserInfoResponse(bannedMember.getOauthId());

        given(kakaoOAuthClient.getKakaoOAuthToken(anyString())).willReturn(tokenResponse);
        given(kakaoOAuthClient.getKakaoUserInfo(tokenResponse)).willReturn(userInfo);
        given(memberRepository.findByOauthIdAndOauthProvider(bannedMember.getOauthId(), Member.OAuthProvider.KAKAO))
                .willReturn(Optional.of(bannedMember));

        // when & then
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            authService.kakaoOAuthLogin("dummyCode");
        });

        assertThat(exception.getMessage()).isEqualTo("영구 정지된 계정은 로그인할 수 없습니다.");
    }
}
