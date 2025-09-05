package com.b4f2.pting.domain.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("auth.oauth2.kakao")
public record KakaoOAuthProperties(

    String clientId,

    String authUri,

    String redirectUri,

    String tokenUri,

    String infoUri
) {

}
