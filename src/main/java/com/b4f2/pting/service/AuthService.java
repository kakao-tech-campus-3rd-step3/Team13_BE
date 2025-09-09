package com.b4f2.pting.service;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import lombok.RequiredArgsConstructor;

import com.b4f2.pting.config.properties.KakaoOAuthProperties;
import com.b4f2.pting.domain.Member;
import com.b4f2.pting.domain.Member.OAuthProvider;
import com.b4f2.pting.dto.AuthResponse;
import com.b4f2.pting.dto.KakaoOAuthTokenResponse;
import com.b4f2.pting.dto.KakaoUserInfoResponse;
import com.b4f2.pting.dto.OAuthUrlResponse;
import com.b4f2.pting.repository.MemberRepository;
import com.b4f2.pting.util.JwtUtil;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final KakaoOAuthProperties kakaoOAuthProperties;
    private final RestClient restClient;
    private final MemberRepository memberRepository;
    private final JwtUtil jwtUtil;

    public OAuthUrlResponse getKakaoOAuthUrl() {
        return new OAuthUrlResponse(
            kakaoOAuthProperties.authUri()
                + "?client_id="
                + kakaoOAuthProperties.clientId()
                + "&redirect_uri="
                + kakaoOAuthProperties.redirectUri()
                + "&response_type=code"
        );
    }

    @Transactional
    public AuthResponse kakaoOAuthLogin(String code) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", kakaoOAuthProperties.clientId());
        params.add("redirect_uri", kakaoOAuthProperties.redirectUri());
        params.add("code", code);

        KakaoOAuthTokenResponse token = restClient.post()
            .uri(kakaoOAuthProperties.tokenUri())
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .body(params)
            .retrieve()
            .body(KakaoOAuthTokenResponse.class);

        KakaoUserInfoResponse userInfo = restClient.post()
            .uri(kakaoOAuthProperties.infoUri())
            .header("Authorization", "Bearer " + token.accessToken())
            .retrieve()
            .body(KakaoUserInfoResponse.class);

        Member member = memberRepository.findByOauthIdAndOauthProvider(userInfo.id(), OAuthProvider.KAKAO)
            .orElseGet(() -> memberRepository.save(new Member(userInfo.id(), OAuthProvider.KAKAO)));

        String jwt = jwtUtil.createToken(member);

        return new AuthResponse(jwt);
    }
}
