package com.b4f2.pting.controller;

import java.util.List;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.firebase.messaging.FirebaseMessagingException;

import lombok.RequiredArgsConstructor;

import com.b4f2.pting.service.FcmService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/push")
public class FcmController {

    private final FcmService fcmService;

    @PostMapping("/single")
    public void testSingleNotification() throws FirebaseMessagingException {
        String title = "FCM 알람 테스트!";
        String body = "테스트입니다~~";
        fcmService.sendSinglePush(
            "fq1nTSy52EwGQnwbpvg-16:APA91bE46Re_2IykaakbJ6fFaCaXcYbG14R8tt15vh-zuqoS3nEdqBSa9o6M9yQ3fPXm7dmCX9p9lSu94lrhIz2Z3CZqSfyZUdG-dxHu9MWaufDnQcQbSak",
            title, body);
    }

    @PostMapping("/multicast")
    public void testMulticastNotification() throws FirebaseMessagingException {
        String title = "FCM 알람 테스트!";
        String body = "테스트입니다~~~";
        fcmService.sendMulticastPush(
            List.of(
                "fq1nTSy52EwGQnwbpvg-16:APA91bE46Re_2IykaakbJ6fFaCaXcYbG14R8tt15vh-zuqoS3nEdqBSa9o6M9yQ3fPXm7dmCX9p9lSu94lrhIz2Z3CZqSfyZUdG-dxHu9MWaufDnQcQbSak"
            ), title, body);
    }
}
