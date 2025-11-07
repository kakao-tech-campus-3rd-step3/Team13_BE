package com.b4f2.pting.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import com.b4f2.pting.config.Login;
import com.b4f2.pting.domain.Member;
import com.b4f2.pting.dto.FcmTokenRequest;
import com.b4f2.pting.service.FcmService;

@RestController
@RequestMapping("/api/v1/fcm")
@RequiredArgsConstructor
@Tag(name = "FCM 푸쉬알림 토큰 API")
public class FcmController {

    private final FcmService fcmService;

    @PostMapping("/token")
    public ResponseEntity<String> saveFcmToken(
            @Parameter(hidden = true) @Login Member member, @RequestBody FcmTokenRequest request) {
        fcmService.saveFcmToken(member, request.token());
        return ResponseEntity.ok("FCM 토큰이 저장되었습니다.");
    }

    @DeleteMapping("/token")
    public ResponseEntity<String> deleteFcmToken(@Parameter(hidden = true) @Login Member member) {
        fcmService.deleteFcmToken(member);
        return ResponseEntity.ok("FCM 토큰이 삭제되었습니다.");
    }
}
