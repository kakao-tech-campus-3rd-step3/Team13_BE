package com.b4f2.pting.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.b4f2.pting.config.properties.GoogleOAuthProperties;
import com.b4f2.pting.config.properties.KakaoOAuthProperties;
import com.b4f2.pting.domain.Member;
import com.b4f2.pting.domain.Member.OAuthProvider;
import com.b4f2.pting.dto.AuthResponse;
import com.b4f2.pting.dto.GoogleOAuthTokenResponse;
import com.b4f2.pting.dto.GoogleUserInfoResponse;
import com.b4f2.pting.dto.KakaoOAuthTokenResponse;
import com.b4f2.pting.dto.KakaoUserInfoResponse;
import com.b4f2.pting.dto.OAuthUrlResponse;
import com.b4f2.pting.repository.MemberRepository;
import com.b4f2.pting.util.GoogleOAuthClient;
import com.b4f2.pting.util.JwtUtil;
import com.b4f2.pting.util.KakaoOAuthClient;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final KakaoOAuthProperties kakaoOAuthProperties;
    private final GoogleOAuthProperties googleOAuthProperties;
    private final KakaoOAuthClient kakaoOAuthClient;
    private final GoogleOAuthClient googleOAuthClient;
    private final MemberRepository memberRepository;
    private final JwtUtil jwtUtil;

    public OAuthUrlResponse getKakaoOAuthUrl() {
        return new OAuthUrlResponse(kakaoOAuthProperties.authUri()
                + "?client_id="
                + kakaoOAuthProperties.clientId()
                + "&redirect_uri="
                + kakaoOAuthProperties.redirectUri()
                + "&response_type=code");
    }

    public OAuthUrlResponse getGoogleOAuthUrl() {
        return new OAuthUrlResponse(googleOAuthProperties.authUri()
                + "?client_id="
                + googleOAuthProperties.clientId()
                + "&redirect_uri="
                + googleOAuthProperties.redirectUri()
                + "&scope=openid&response_type=code");
    }

    @Transactional
    public AuthResponse kakaoOAuthLogin(String code) {
        KakaoOAuthTokenResponse token = kakaoOAuthClient.getKakaoOAuthToken(code);
        KakaoUserInfoResponse userInfo = kakaoOAuthClient.getKakaoUserInfo(token);

        Member member = memberRepository
                .findByOauthIdAndOauthProvider(userInfo.id(), OAuthProvider.KAKAO)
                .orElseGet(() -> memberRepository.save(new Member(userInfo.id(), OAuthProvider.KAKAO)));

        String jwt = jwtUtil.createToken(member);

        return new AuthResponse(jwt);
    }

    @Transactional
    public AuthResponse googleOAuthLogin(String code) {
        GoogleOAuthTokenResponse token = googleOAuthClient.getGoogleOAuthToken(code);
        GoogleUserInfoResponse userInfo = googleOAuthClient.getGoogleUserInfo(token);

        Member member = memberRepository
                .findByOauthIdAndOauthProvider(userInfo.id(), OAuthProvider.GOOGLE)
                .orElseGet(() -> memberRepository.save(new Member(userInfo.id(), OAuthProvider.GOOGLE)));

        String jwt = jwtUtil.createToken(member);

        return new AuthResponse(jwt);
    }
}
