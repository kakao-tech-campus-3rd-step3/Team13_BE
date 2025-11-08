package com.b4f2.pting.config;

import java.time.Duration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    private static final int CONNECTION_TIMEOUT = 3;
    private static final int READ_TIMEOUT = 3;

    @Bean
    RestClient restClient() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofSeconds(CONNECTION_TIMEOUT));
        requestFactory.setReadTimeout(Duration.ofSeconds(READ_TIMEOUT));

        return RestClient.builder().requestFactory(requestFactory).build();
    }
}
