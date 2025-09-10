package com.b4f2.pting.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.b4f2.pting.domain.Member;
import com.b4f2.pting.domain.School;
import com.b4f2.pting.dto.CertificationRequest;
import com.b4f2.pting.dto.CertificationResponse;
import com.b4f2.pting.util.JwtUtil;

@ExtendWith(MockitoExtension.class)
public class CertificationServiceTest {

    @Mock
    private MemberService memberService;

    @Mock
    private EmailService emailService;

    @Mock
    private SchoolService schoolService;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private CertificationService certificationService;

    private Member member;
    private School school;

    @BeforeEach
    void setUp() {
        member = new Member(1L, Member.OAuthProvider.KAKAO);
        ReflectionTestUtils.setField(member, "id", 1L);

        school = new School("부산대학교", "pusan.ac.kr");
        ReflectionTestUtils.setField(school, "id", 1L);

        member.updateSchool(school);
    }

    @Test
    void sendCertificationEmail_이메일보내기_성공() {
        // given
        String schoolEmail = "test@pusan.ac.kr";
        String emailToken = "email_token";
        CertificationRequest request = new CertificationRequest("test");

        given(jwtUtil.createEmailToken(member, schoolEmail)).willReturn(emailToken);

        // when
        certificationService.sendCertificationEmail(member, request);

        // then
        verify(emailService).sendCertificationEmail(eq(schoolEmail), eq(emailToken));
    }

    @Test
    void sendCertficiationEmail_학교미선택_예외발생() {
        // given
        member.updateSchool(null);
        CertificationRequest request = new CertificationRequest("test");

        // when & then
        assertThatThrownBy(() -> certificationService.sendCertificationEmail(member, request))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("학교를 먼저 선택해야 합니다.");
    }

    @Test
    void sendCertificationEmail_학교이메일아님_예외발생() {
        // given
        School wrongSchool = new School("구글", "gmail.com");
        ReflectionTestUtils.setField(wrongSchool, "id", 2L);
        member.updateSchool(wrongSchool);
        CertificationRequest request = new CertificationRequest("test");

        // when & then
        assertThatThrownBy(() -> certificationService.sendCertificationEmail(member, request))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("학교 이메일만 인증 가능합니다.");
    }

    @Test
    void sendCertificationEmail_이미인증된이메일_예외발생() {
        // given
        member.updateSchoolEmail("test@pusan.ac.kr");
        member.markAsVerified();
        CertificationRequest request = new CertificationRequest("test");

        // when & then
        assertThatThrownBy(() -> certificationService.sendCertificationEmail(member, request))
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

        when(memberService.getMemberById(member.getId())).thenReturn(member);
        doAnswer(invocation -> {
            Member m = invocation.getArgument(0);
            m.updateSchoolEmail(schoolEmail);
            m.markAsVerified();
            return null;
        }).when(memberService).verifySchoolEmail(member, schoolEmail);

        // when
        CertificationResponse response = certificationService.verifyCertification(token);

        // then
        assertNotNull(response);
        assertTrue(response.isVerified());

        verify(memberService, times(1)).verifySchoolEmail(member, schoolEmail);
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
