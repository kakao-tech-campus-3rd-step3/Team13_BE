package com.b4f2.pting.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.b4f2.pting.domain.Member;
import com.b4f2.pting.dto.CertificationResponse;
import com.b4f2.pting.repository.MemberRepository;
import com.b4f2.pting.util.JwtUtil;

@ExtendWith(MockitoExtension.class)
public class CertificationServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private CertificationService certificationService;

    private Member member;

    @BeforeEach
    void setUp() {
        member = new Member(1L, Member.OAuthProvider.KAKAO);
        ReflectionTestUtils.setField(member, "id", 1L);
    }

    @Test
    void sendCertificationEmail_이메일보내기_성공() {
        // given
        String schoolEmail = "test@pusan.ac.kr";
        String emailToken = "email_token";

        given(jwtUtil.createEmailToken(member)).willReturn(emailToken);

        // when
        certificationService.sendCertificationEmail(schoolEmail, member);

        // then
        verify(emailService).sendCertificationEmail(eq(schoolEmail), eq(emailToken));
    }

    @Test
    void sendCertificationEmail_학교이메일아님_예외발생() {
        // given
        String wrongEmail = "test@gmail.com";

        // when & then
        assertThatThrownBy(() -> certificationService.sendCertificationEmail(wrongEmail, member))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("학교 이메일만 인증 가능합니다.");
    }

    @Test
    void sendCertificationEmail_이미인증된이메일_예외발생() {
        // given
        String verifiedEmail = "test@pusan.ac.kr";
        member.updateSchoolEmail(verifiedEmail);
        member.markAsVerified();

        // when & then
        assertThatThrownBy(() -> certificationService.sendCertificationEmail(verifiedEmail, member))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("이미 인증된 이메일입니다.");
    }

    @Test
    void verifyCertification_인증하기_성공() {
        // given
        String token = "token";
        String schoolEmail = "test@pusan.ac.kr";

        given(jwtUtil.getMemberId(token)).willReturn(member.getId());
        given(jwtUtil.getSchoolEmail(token)).willReturn(schoolEmail);
        given(memberRepository.findById(member.getId())).willReturn(Optional.of(member));

        // when
        CertificationResponse response = certificationService.verifyCertification(token);

        // then
        assertThat(response.isVerified()).isTrue();
        assertThat(member.getSchoolEmail()).isEqualTo(schoolEmail);
    }

    @Test
    void checkCertification_인증여부조회_성공() {
        // given
        member.markAsVerified();

        // when
        CertificationResponse response = certificationService.checkCertification(member);

        // then
        assertThat(response.isVerified()).isTrue();
    }

    @Test
    void checkCertification_인증여부조회_실패() {
        // given
        // member는 기본 false

        // when
        CertificationResponse response = certificationService.checkCertification(member);

        // then
        assertThat(response.isVerified()).isFalse();
    }
}
