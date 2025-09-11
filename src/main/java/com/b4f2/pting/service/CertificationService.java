package com.b4f2.pting.service;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.b4f2.pting.domain.Member;
import com.b4f2.pting.dto.CertificationResponse;
import com.b4f2.pting.repository.MemberRepository;
import com.b4f2.pting.util.JwtUtil;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CertificationService {

    private final MemberRepository memberRepository;
    private final EmailService emailService;
    private final JwtUtil jwtUtil;

    public void sendCertificationEmail(String email, Member member) {
        if (!isValidSchoolEmail(email)) {
            throw new IllegalArgumentException("학교 이메일만 인증 가능합니다.");
        }

        if (member.getIsVerified() && email.equals(member.getSchoolEmail())) {
            throw new IllegalArgumentException("이미 인증된 이메일입니다.");
        }

        String EmailToken = jwtUtil.createEmailToken(member);

        emailService.sendCertificationEmail(email, EmailToken);
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

        Member member = memberRepository.findById(tokenMemberId)
            .orElseThrow(() -> new EntityNotFoundException("회원 정보가 존재하지 않습니다."));

        member.updateVerifiedSchoolEmail(tokenSchoolEmail);
        member.markAsVerified();

        memberRepository.save(member);

        return new CertificationResponse(member.getIsVerified());
    }

    public CertificationResponse checkCertification(Member member) {
        return new CertificationResponse(member.getIsVerified());
    }

    private boolean isValidSchoolEmail(String email) {
        return email != null && email.endsWith(".ac.kr");
    }

    // TODO: Exception 재설정
}
