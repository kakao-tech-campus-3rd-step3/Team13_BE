package com.b4f2.pting.controller;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import com.b4f2.pting.config.Login;
import com.b4f2.pting.domain.Member;
import com.b4f2.pting.domain.School;
import com.b4f2.pting.dto.CertificationRequest;
import com.b4f2.pting.dto.CertificationResponse;
import com.b4f2.pting.dto.CertificationVerifyRequest;
import com.b4f2.pting.dto.SchoolResponse;
import com.b4f2.pting.facade.CertificationService;
import com.b4f2.pting.service.SchoolService;

@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
public class MemberController {

    private final CertificationService certificationService;
    private final SchoolService schoolService;

    @PostMapping("/me/school/{schoolId}")
    public ResponseEntity<SchoolResponse> selectSchool(@Login Member member, @PathVariable Long schoolId) {
        School school = schoolService.selectSchool(member, schoolId);
        SchoolResponse response = new SchoolResponse(school.getId(), school.getName(), school.getPostfix());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/me/certification/email")
    public ResponseEntity<String> sendCertificationEmail(
            @Login Member member, @RequestBody CertificationRequest request) {
        certificationService.sendCertificationEmail(member, request);
        return ResponseEntity.ok("인증 메일을 발송했습니다. 메일을 확인하세요.");
    }

    @PostMapping("/me/certification/verify")
    public ResponseEntity<CertificationResponse> verifyCertification(
            @Login Member member, @RequestBody @Valid CertificationVerifyRequest request) {
        CertificationResponse response = certificationService.verifyCertification(member, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me/certification/status")
    public ResponseEntity<CertificationResponse> checkCertification(@Login Member member) {
        CertificationResponse response = certificationService.checkCertification(member);
        return ResponseEntity.ok(response);
    }
}
