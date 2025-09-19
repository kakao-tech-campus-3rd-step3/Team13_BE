package com.b4f2.pting.config.properties;

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
