package com.b4f2.pting.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import com.b4f2.pting.config.Login;
import com.b4f2.pting.domain.Member;
import com.b4f2.pting.dto.CertificationRequest;
import com.b4f2.pting.dto.CertificationResponse;
import com.b4f2.pting.dto.SchoolResponse;
import com.b4f2.pting.service.CertificationService;
import com.b4f2.pting.service.SchoolService;

@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
public class MemberController {

    private final CertificationService certificationService;
    private final SchoolService schoolService;

    @PostMapping("/me/certification")
    public ResponseEntity<String> requestCertification(
        @Login Member member,
        @RequestBody CertificationRequest certificationRequest
    ) {
        String email = certificationRequest.schoolEmail();
        certificationService.sendCertificationEmail(email, member);
        return ResponseEntity.ok("인증 메일을 발송했습니다. 메일을 확인하세요.");
    }

    @GetMapping("/me/certification/verify")
    public ResponseEntity<CertificationResponse> verifyCertification(@RequestParam String token) {
        CertificationResponse certificationResponse = certificationService.verifyCertification(token);
        return ResponseEntity.ok(certificationResponse);
    }

    @GetMapping("/me/certification")
    public ResponseEntity<CertificationResponse> checkCertification(@Login Member member) {
        CertificationResponse certificationResponse = certificationService.checkCertification(member);
        return ResponseEntity.ok(certificationResponse);
    }

    @PostMapping("/me/school/{schoolId}")
    public ResponseEntity<SchoolResponse> selectSchool(@Login Member member, @PathVariable Long schoolId) {
        SchoolResponse response = schoolService.selectSchool(member, schoolId);
        return ResponseEntity.ok(response);
    }
}
