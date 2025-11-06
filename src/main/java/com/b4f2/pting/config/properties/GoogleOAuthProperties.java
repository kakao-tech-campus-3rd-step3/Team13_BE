package com.b4f2.pting.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("auth.oauth2.google")
public record GoogleOAuthProperties(
        String clientId, String clientSecret, String authUri, String redirectUri, String tokenUri, String infoUri) {}
