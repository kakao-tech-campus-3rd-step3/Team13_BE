package com.b4f2.pting.facade;

import java.util.Random;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.b4f2.pting.config.InMemoryCache;
import com.b4f2.pting.domain.Member;
import com.b4f2.pting.domain.School;
import com.b4f2.pting.dto.CertificationRequest;
import com.b4f2.pting.dto.CertificationResponse;
import com.b4f2.pting.dto.CertificationVerifyRequest;
import com.b4f2.pting.service.EmailService;
import com.b4f2.pting.util.EmailUtil;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CertificationService {

    private static final long CODE_EXPIRE_TIME = 5 * 60 * 1000;
    private static final Random random = new Random();

    private final EmailService emailService;
    private final InMemoryCache cache;
    private final EmailUtil emailUtil;

    public void sendCertificationEmail(Member member, CertificationRequest request) {
        School school = getSchoolOrThrowException(member);

        String schoolEmail = emailUtil.getEmailAddress(request.localPart(), school.getPostfix());

        if (!isValidSchoolEmail(schoolEmail)) {
            throw new IllegalArgumentException("학교 이메일만 인증 가능합니다.");
        }

        if (member.isVerifiedEmail(schoolEmail)) {
            throw new IllegalStateException("이미 인증된 이메일입니다.");
        }

        String key = emailUtil.getEmailCertificationKey(member.getId(), schoolEmail);
        cache.delete(key);
        String code = generateRandomCode();
        cache.set(key, code, CODE_EXPIRE_TIME);

        emailService.sendCertificationEmail(schoolEmail, code);
    }

    private boolean isValidSchoolEmail(String email) {
        return email != null && emailUtil.isSchoolEmail(email);
    }

    private String generateRandomCode() {
        int code = random.nextInt(900000) + 100000;
        return String.valueOf(code);
    }

    @Transactional
    public CertificationResponse verifyCertification(Member member, CertificationVerifyRequest request) {
        School school = getSchoolOrThrowException(member);

        String schoolEmail = emailUtil.getEmailAddress(request.localPart(), school.getPostfix());
        String code = request.code();

        String key = emailUtil.getEmailCertificationKey(member.getId(), schoolEmail);
        String savedCode = cache.get(key);

        if (savedCode == null) {
            throw new IllegalArgumentException("인증 코드가 만료되었습니다.");
        }
        if (!savedCode.equals(code)) {
            throw new IllegalArgumentException("인증 코드가 일치하지 않습니다.");
        }

        member.updateSchoolEmail(schoolEmail);
        member.markAsVerified();

        cache.delete(key);

        return new CertificationResponse(member.getIsVerified());
    }

    private School getSchoolOrThrowException(Member member) {
        return member.getSchool().orElseThrow(() -> new IllegalStateException("학교를 먼저 선택해야 합니다."));
    }

    public CertificationResponse checkCertification(Member member) {
        return new CertificationResponse(member.getIsVerified());
    }

    // TODO: Exception 재설정
}
