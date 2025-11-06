package com.b4f2.pting.util;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import lombok.RequiredArgsConstructor;

import com.b4f2.pting.config.properties.KakaoOAuthProperties;
import com.b4f2.pting.dto.KakaoOAuthTokenResponse;
import com.b4f2.pting.dto.KakaoUserInfoResponse;

@Component
@RequiredArgsConstructor
public class KakaoOAuthClient {

    private final RestClient restClient;
    private final KakaoOAuthProperties kakaoOAuthProperties;

    public KakaoOAuthTokenResponse getKakaoOAuthToken(String code) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", kakaoOAuthProperties.clientId());
        params.add("client_secret", kakaoOAuthProperties.clientSecret());
        params.add("redirect_uri", kakaoOAuthProperties.redirectUri());
        params.add("code", code);

        return restClient
                .post()
                .uri(kakaoOAuthProperties.tokenUri())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(params)
                .retrieve()
                .body(KakaoOAuthTokenResponse.class);
    }

    public KakaoUserInfoResponse getKakaoUserInfo(KakaoOAuthTokenResponse token) {
        return restClient
                .post()
                .uri(kakaoOAuthProperties.infoUri())
                .header("Authorization", "Bearer " + token.accessToken())
                .retrieve()
                .body(KakaoUserInfoResponse.class);
    }
}
