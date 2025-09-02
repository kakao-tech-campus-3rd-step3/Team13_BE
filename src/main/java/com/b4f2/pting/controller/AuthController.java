package com.b4f2.pting.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import com.b4f2.pting.dto.OAuthUrlResponse;
import com.b4f2.pting.service.AuthService;

@RestController
@RequestMapping("api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @GetMapping("/kakao")
    public OAuthUrlResponse getKakaoOAuthUrl() {
        return authService.getKakaoOAuthUrl();
    }
}
