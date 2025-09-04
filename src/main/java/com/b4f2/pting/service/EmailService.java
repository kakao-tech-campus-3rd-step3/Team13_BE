package com.b4f2.pting.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.domain}")
    private String domain;

    public void sendCertificationEmail(String toEmail, String token) {
        String subject = "[Pting] 학교 이메일 인증 요청";

        String certificationUrl = UriComponentsBuilder
            .fromHttpUrl(domain + "/api/v1/members/mine/certification/verify")
            .queryParam("token", token)
            .toUriString();

        String message = "안녕하세요!\n\n" +
            "아래 링크를 클릭하여 학교 이메일 인증을 완료해주세요.\n" +
            certificationUrl + "\n\n" +
            "링크는 30분 동안만 유효합니다.";

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(toEmail);
        mailMessage.setSubject(subject);
        mailMessage.setText(message);

        mailSender.send(mailMessage);
    }
}
