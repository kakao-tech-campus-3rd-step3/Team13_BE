package com.b4f2.pting.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.firebase.messaging.FirebaseMessagingException;

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

    @PostMapping("/single")
    public void testSingleNotification() throws FirebaseMessagingException {
        String title = "FCM 알람 테스트!";
        String body = "테스트입니다~~";
        fcmService.sendSinglePush(
            "token",
            title, body);
    }

    @PostMapping("/multicast")
    public void testMulticastNotification() throws FirebaseMessagingException {
        String title = "FCM 알람 테스트!";
        String body = "테스트입니다~~~";
        fcmService.sendMulticastPush(
            List.of(
                "token1",
                "token2"
            ), title, body);
    }
}
