package com.b4f2.pting.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailService emailService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(emailService, "domain", "http://localhost:8080");
    }

    @Test
    void sendCertificationEmail_이메일보내기_성공() {
        // given
        String toEmail = "test@pusan.ac.kr";
        String code = "123456";

        EmailService spyService = Mockito.spy(emailService);
        String templateContent = "안녕하세요!\n인증 링크: {code}";
        doReturn(templateContent).when(spyService).loadTemplate("school_verification_email.txt");

        // when
        spyService.sendCertificationEmail(toEmail, code);

        // then
        ArgumentCaptor<SimpleMailMessage> mailMessageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(mailMessageCaptor.capture());

        SimpleMailMessage sentMessage = mailMessageCaptor.getValue();

        assertThat(sentMessage.getTo()).containsExactly(toEmail);
        assertThat(sentMessage.getSubject()).isEqualTo("[Pting] 학교 이메일 인증 요청");
        assertThat(sentMessage.getText()).contains(code);
    }

    @Test
    void loadTemplate_파일없음_예외발생() {
        // given
        EmailService spyService = Mockito.spy(emailService);

        // when & then
        assertThatThrownBy(() -> spyService.loadTemplate("not_exist.txt"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("메일 템플릿 로드 실패");
    }
}
