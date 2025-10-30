package com.b4f2.pting.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import com.b4f2.pting.config.Login;
import com.b4f2.pting.domain.Member;
import com.b4f2.pting.dto.FcmTokenRequest;
import com.b4f2.pting.service.FcmService;

@RestController
@RequestMapping("/api/v1/fcm")
@RequiredArgsConstructor
public class FcmController {

    private final FcmService fcmService;

    @PostMapping("/token")
    public ResponseEntity<String> saveToken(@Login Member member, @RequestBody FcmTokenRequest request) {
        fcmService.saveToken(member, request.token());
        return ResponseEntity.ok("FCM 토큰이 저장되었습니다.");
    }

    @DeleteMapping("/token")
    public ResponseEntity<String> deleteToken(@Login Member member) {
        fcmService.deleteToken(member);
        return ResponseEntity.ok("FCM 토큰이 삭제되었습니다.");
    }
}
