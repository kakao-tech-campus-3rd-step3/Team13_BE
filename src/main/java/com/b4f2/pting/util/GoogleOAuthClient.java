package com.b4f2.pting.util;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import lombok.RequiredArgsConstructor;

import com.b4f2.pting.config.properties.GoogleOAuthProperties;
import com.b4f2.pting.dto.GoogleOAuthTokenResponse;
import com.b4f2.pting.dto.GoogleUserInfoResponse;

@Component
@RequiredArgsConstructor
public class GoogleOAuthClient {

    private final RestClient restClient;
    private final GoogleOAuthProperties googleOAuthPrpoerties;

    public GoogleOAuthTokenResponse getGoogleOAuthToken(String code) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", googleOAuthPrpoerties.clientId());
        params.add("client_secret", googleOAuthPrpoerties.clientSecret());
        params.add("redirect_uri", googleOAuthPrpoerties.redirectUri());
        params.add("code", code);

        return restClient
                .post()
                .uri(googleOAuthPrpoerties.tokenUri())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(params)
                .retrieve()
                .body(GoogleOAuthTokenResponse.class);
    }

    public GoogleUserInfoResponse getGoogleUserInfo(GoogleOAuthTokenResponse token) {
        return restClient
                .post()
                .uri(googleOAuthPrpoerties.infoUri())
                .header("Authorization", "Bearer " + token.accessToken())
                .retrieve()
                .body(GoogleUserInfoResponse.class);
    }
}
