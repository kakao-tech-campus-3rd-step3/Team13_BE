package com.b4f2.pting.controller;

import com.b4f2.pting.dto.CertificationRequest;
import com.b4f2.pting.dto.CertificationResponse;
import com.b4f2.pting.service.CertificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class CertificationController {

    private final CertificationService certificationService;

    @PostMapping("/members/mine/certification")
    public ResponseEntity<String> requestCertification(
        @RequestBody CertificationRequest certificationRequest
    ) {
        String email = certificationRequest.schoolEmail();
        String token = certificationRequest.token();
        certificationService.sendCertificationEmail(email, token);
        return ResponseEntity.ok("인증 메일을 발송했습니다. 메일을 확인하세요.");
    }

    @GetMapping("/members/mine/certification/verify")
    public ResponseEntity<CertificationResponse> verifyCertification(@RequestParam String token) {
        CertificationResponse certificationResponse = certificationService.verifyCertification(token);
        return ResponseEntity.ok(certificationResponse);
    }
}
