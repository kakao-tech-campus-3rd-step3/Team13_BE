package com.b4f2.pting.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.MulticastMessage;
import com.google.firebase.messaging.Notification;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FcmService {

    public void sendSinglePush(String token, String title, String body) throws FirebaseMessagingException {
        Message message = Message.builder()
            .setNotification(Notification.builder()
                .setTitle(title)
                .setBody(body)
                .build())
            .setToken(token) // 메시지를 받을 유저 브라우저의 FCM 토큰
            .build();

        FirebaseMessaging.getInstance().send(message);
    }

    public void sendMulticastPush(List<String> tokens, String title, String body) throws FirebaseMessagingException {
        MulticastMessage message = MulticastMessage.builder()
            .setNotification(Notification.builder()
                .setTitle(title)
                .setBody(body)
                .build())
            .addAllTokens(tokens) // 메시지를 받을 유저 브라우저의 FCM 토큰 리스트
            .build();

        FirebaseMessaging.getInstance().sendEachForMulticast(message);
    }
}
