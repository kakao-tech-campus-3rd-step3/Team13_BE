package com.b4f2.pting.service;

import static com.b4f2.pting.domain.Member.OAuthProvider.KAKAO;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.MulticastMessage;

import com.b4f2.pting.domain.FcmToken;
import com.b4f2.pting.domain.Member;
import com.b4f2.pting.repository.FcmTokenRepository;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class FcmServiceTest {

    @Mock
    private FcmTokenRepository fcmTokenRepository;

    @InjectMocks
    private FcmService fcmService;

    @Test
    void saveToken_토큰이_없는_회원의_FCM_토큰_저장_성공() {
        // given
        Member member = new Member(123123L, KAKAO);
        String token = "fcm-token";
        given(fcmTokenRepository.findByMember(member)).willReturn(Optional.empty());

        // when
        fcmService.saveFcmToken(member, token);

        // then
        verify(fcmTokenRepository).save(any(FcmToken.class));
    }

    @Test
    void saveToken_토큰이_있는_회원의_FCM_토큰_업데이트_성공() {
        // given
        Member member = new Member(123131L, KAKAO);
        String oldToken = "fcm-token-1";
        String newToken = "fcm-token-2";
        FcmToken fcmToken = new FcmToken(member, oldToken);

        given(fcmTokenRepository.findByMember(member)).willReturn(Optional.of(fcmToken));

        // when
        fcmService.saveFcmToken(member, newToken);

        // then
        assertThat(fcmToken.getToken()).isEqualTo(newToken);
        verify(fcmTokenRepository, times(0)).save(any(FcmToken.class));
    }

    @Test
    void sendSinglePush_단일_푸시_메시지_전송_성공() throws FirebaseMessagingException {
        // given
        String token = "fcm-token";
        String title = "테스트 제목";
        String body = "테스트 내용";

        FirebaseMessaging firebaseMessaging = mock(FirebaseMessaging.class);

        try (MockedStatic<FirebaseMessaging> mockStatic = mockStatic(FirebaseMessaging.class)) {
            mockStatic.when(FirebaseMessaging::getInstance).thenReturn(firebaseMessaging);

            // when
            fcmService.sendSinglePush(token, title, body);

            // then
            verify(firebaseMessaging).send(any(Message.class));
        }
    }

    @Test
    void sendMulticastPush_다중_푸시_메시지_전송_성공() throws FirebaseMessagingException {
        // given
        List<String> tokens = List.of("token1", "token2", "token3");
        String title = "테스트 제목";
        String body = "테스트 내용";

        FirebaseMessaging firebaseMessaging = mock(FirebaseMessaging.class);

        try (MockedStatic<FirebaseMessaging> mockStatic = mockStatic(FirebaseMessaging.class)) {
            mockStatic.when(FirebaseMessaging::getInstance).thenReturn(firebaseMessaging);

            // when
            fcmService.sendMulticastPush(tokens, title, body);

            // then
            verify(firebaseMessaging).sendEachForMulticast(any(MulticastMessage.class));
        }
    }
}
