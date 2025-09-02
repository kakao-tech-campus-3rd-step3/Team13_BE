package com.b4f2.pting.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.b4f2.pting.domain.properties.KakaoOAuthProperties;
import com.b4f2.pting.dto.OAuthUrlResponse;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final KakaoOAuthProperties kakaoOAuthProperties;

    @Transactional(readOnly = true)
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
}
