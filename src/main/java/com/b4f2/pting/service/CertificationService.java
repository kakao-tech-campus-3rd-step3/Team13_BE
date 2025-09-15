package com.b4f2.pting.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.b4f2.pting.config.InMemoryCache;
import com.b4f2.pting.domain.Member;
import com.b4f2.pting.domain.School;
import com.b4f2.pting.dto.CertificationRequest;
import com.b4f2.pting.dto.CertificationResponse;
import com.b4f2.pting.dto.CertificationVerifyRequest;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CertificationService {

    private final EmailService emailService;
    private final MemberService memberService;
    private final InMemoryCache cache;

    private static final long CODE_EXPIRE_TIME = 5 * 60 * 1000;

    public void sendCertificationEmail(Member member, CertificationRequest request) {
        School school = member.getSchool();
        if (school == null) {
            throw new IllegalStateException("학교를 먼저 선택해야 합니다.");
        }

        String schoolEmail = request.localPart() + "@" + school.getDomain();
        if (!isValidSchoolEmail(schoolEmail)) {
            throw new IllegalArgumentException("학교 이메일만 인증 가능합니다.");
        }

        if (member.getIsVerified() && member.isMySchoolEmail(schoolEmail)) {
            throw new IllegalStateException("이미 인증된 이메일입니다.");
        }

        String key = "cert:" + member.getId() + ":" + schoolEmail;
        cache.delete(key);
        String code = generateRandomCode();
        cache.set(key, code, CODE_EXPIRE_TIME);

        emailService.sendCertificationEmail(schoolEmail, code);
    }

    private boolean isValidSchoolEmail(String email) {
        return email != null && email.endsWith(".ac.kr");
    }

    private String generateRandomCode() {
        return String.valueOf((int) (Math.random() * 900000) + 100000);
    }

    @Transactional
    public CertificationResponse verifyCertification(Member member, CertificationVerifyRequest request) {
        School school = member.getSchool();
        if (school == null) {
            throw new IllegalStateException("학교를 먼저 선택해야 합니다.");
        }

        String schoolEmail = request.localPart() + "@" + school.getDomain();
        String code = request.code();

        String key = "cert:" + member.getId() + ":" + schoolEmail;
        String savedCode = cache.get(key);

        if (savedCode == null) {
            throw new IllegalArgumentException("인증 코드가 만료되었습니다.");
        }
        if (!savedCode.equals(code)) {
            throw new IllegalArgumentException("인증 코드가 일치하지 않습니다.");
        }

        memberService.verifySchoolEmail(member, schoolEmail);

        cache.delete(key);

        return new CertificationResponse(member.getIsVerified());
    }

    public CertificationResponse checkCertification(Member member) {
        return new CertificationResponse(member.getIsVerified());
    }

    // TODO: Exception 재설정
}
