package com.b4f2.pting.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.b4f2.pting.domain.Member;
import com.b4f2.pting.domain.School;
import com.b4f2.pting.dto.CertificationRequest;
import com.b4f2.pting.dto.CertificationResponse;
import com.b4f2.pting.util.JwtUtil;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CertificationService {

    private final EmailService emailService;
    private final MemberService memberService;
    private final SchoolService schoolService;
    private final JwtUtil jwtUtil;

    public void sendCertificationEmail(Member member, CertificationRequest request) {
        School school = member.getSchool();
        if (school == null) {
            throw new IllegalStateException("학교를 먼저 선택해야 합니다.");
        }

        String schoolEmail = request.localPart() + "@" + school.getDomain();
        if (!isValidSchoolEmail(schoolEmail)) {
            throw new IllegalArgumentException("학교 이메일만 인증 가능합니다.");
        }

        if (member.getIsVerified() && schoolEmail.equals(member.getSchoolEmail())) {
            throw new IllegalStateException("이미 인증된 이메일입니다.");
        }

        String emailToken = jwtUtil.createEmailToken(member, schoolEmail);

        emailService.sendCertificationEmail(schoolEmail, emailToken);
    }

    private boolean isValidSchoolEmail(String email) {
        return email != null && email.endsWith(".ac.kr");
    }

    @Transactional
    public CertificationResponse verifyCertification(String token) {
        Long tokenMemberId;
        String tokenSchoolEmail;
        try {
            tokenMemberId = jwtUtil.getMemberId(token);
            tokenSchoolEmail = jwtUtil.getSchoolEmail(token);
        } catch (Exception e) {
            throw new IllegalArgumentException("토큰이 유효하지 않거나 만료되었습니다.");
        }

        Member member = memberService.getMemberById(tokenMemberId);
        memberService.verifySchoolEmail(member, tokenSchoolEmail);

        return new CertificationResponse(member.getIsVerified());
    }

    public CertificationResponse checkCertification(Member member) {
        return new CertificationResponse(member.getIsVerified());
    }

    // TODO: Exception 재설정
}
