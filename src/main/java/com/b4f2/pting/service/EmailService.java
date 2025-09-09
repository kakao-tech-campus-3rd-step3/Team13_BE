package com.b4f2.pting.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.domain}")
    private String domain;

    String loadTemplate(String fileName) {
        try {
            Path path = Path.of("src/main/resources/templates/" + fileName);
            return Files.readString(path);
        } catch (IOException e) {
            throw new RuntimeException("메일 템플릿 로드 실패");
        }
    }

    public void sendCertificationEmail(String toEmail, String token) {
        String subject = "[Pting] 학교 이메일 인증 요청";

        String certificationUrl = UriComponentsBuilder
            .fromHttpUrl(domain + "/api/v1/members/me/certification/verify")
            .queryParam("token", token)
            .toUriString();

        String template = loadTemplate("school_verification_email.txt");

        String message = template.replace("{link}", certificationUrl);

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(toEmail);
        mailMessage.setSubject(subject);
        mailMessage.setText(message);

        mailSender.send(mailMessage);
    }
}
