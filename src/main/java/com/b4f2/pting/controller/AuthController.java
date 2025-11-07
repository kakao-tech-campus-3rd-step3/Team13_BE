package com.b4f2.pting.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import com.b4f2.pting.dto.AuthResponse;
import com.b4f2.pting.dto.OAuthUrlResponse;
import com.b4f2.pting.service.AuthService;

@RestController
@RequestMapping("api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "OAuth 회원가입 API")
public class AuthController {

    private final AuthService authService;

    @GetMapping("/kakao")
    public OAuthUrlResponse getKakaoOAuthUrl() {
        return authService.getKakaoOAuthUrl();
    }

    @GetMapping("/google")
    public OAuthUrlResponse getGoogleOAuthUrl() {
        return authService.getGoogleOAuthUrl();
    }

    @GetMapping("/kakao/callback")
    public ResponseEntity<AuthResponse> kakaoOAuthCallback(@RequestParam String code) {
        AuthResponse authResponse = authService.kakaoOAuthLogin(code);
        return ResponseEntity.ok(authResponse);
    }

    @GetMapping("/google/callback")
    public ResponseEntity<AuthResponse> googleOAuthCallback(@RequestParam String code) {
        AuthResponse authResponse = authService.googleOAuthLogin(code);
        return ResponseEntity.ok(authResponse);
    }
}
