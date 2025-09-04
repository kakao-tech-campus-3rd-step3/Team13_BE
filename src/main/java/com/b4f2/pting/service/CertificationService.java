package com.b4f2.pting.service;

import com.b4f2.pting.domain.Member;
import com.b4f2.pting.dto.CertificationResponse;
import com.b4f2.pting.repository.MemberRepository;
import com.b4f2.pting.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CertificationService {

    private final MemberRepository memberRepository;
    private final EmailService emailService;
    private final JwtUtil jwtUtil;

    public boolean isValidSchoolEmail(String email) {
        return email != null && email.endsWith(".ac.kr");
    }

    public void sendCertificationEmail(String email, String token) {
        if (!isValidSchoolEmail(email)) {
            throw new IllegalArgumentException("학교 이메일만 인증 가능합니다.");
        }

        Long tokenMemberId;
        try {
            tokenMemberId = jwtUtil.getMemberId(token);
        } catch (Exception e) {
            throw new IllegalArgumentException("토큰이 유효하지 않거나 만료되었습니다.");
        }

        Member member = memberRepository.findById(tokenMemberId)
            .orElseThrow(() -> new RuntimeException("유저가 존재하지 않습니다."));

        if (member.getIsVerified() && email.equals(member.getSchoolEmail())) {
            throw new IllegalArgumentException("이미 인증된 이메일입니다.");
        }

        String EmailToken = jwtUtil.createEmailToken(member);

        emailService.sendCertificationEmail(email, EmailToken);
    }

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
            .orElseThrow(() -> new RuntimeException("유저가 존재하지 않습니다."));

        if (!member.getId().equals(tokenMemberId)) {
            throw new IllegalArgumentException("토큰이 유효하지 않습니다.");
        }

        member.updateVerifiedSchoolEmail(tokenSchoolEmail);
        member.markAsVerified();

        memberRepository.save(member);

        return new CertificationResponse(member.getIsVerified());
    }

    // TODO: Exception 재설정
}
