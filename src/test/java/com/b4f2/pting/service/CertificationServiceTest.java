package com.b4f2.pting.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.b4f2.pting.config.InMemoryCache;
import com.b4f2.pting.domain.Member;
import com.b4f2.pting.domain.School;
import com.b4f2.pting.dto.CertificationRequest;
import com.b4f2.pting.dto.CertificationResponse;
import com.b4f2.pting.dto.CertificationVerifyRequest;
import com.b4f2.pting.facade.CertificationService;
import com.b4f2.pting.util.EmailUtil;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class CertificationServiceTest {

    @Mock
    private EmailService emailService;

    @Mock
    private InMemoryCache cache;

    @Mock
    private EmailUtil emailUtil;

    @InjectMocks
    private CertificationService certificationService;

    private Member member;
    private School school;

    private String localPart = "test";
    private String code = "123456";
    private static final long CODE_EXPIRE_TIME = 5 * 60 * 1000;

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
        String schoolEmail = localPart + "@" + school.getPostfix();
        CertificationRequest request = new CertificationRequest(localPart);

        given(emailUtil.getEmailAddress(localPart, school.getPostfix())).willReturn(schoolEmail);
        given(emailUtil.getEmailCertificationKey(member.getId(), schoolEmail)).willReturn(
            "cert:" + member.getId() + ":" + schoolEmail);
        given(emailUtil.isSchoolEmail(schoolEmail)).willReturn(true);

        doNothing().when(cache).delete(anyString());
        doNothing().when(cache).set(anyString(), anyString(), anyLong());
        doNothing().when(emailService).sendCertificationEmail(anyString(), anyString());

        // when
        certificationService.sendCertificationEmail(member, request);

        // then
        verify(cache, times(1)).delete(anyString());
        verify(cache, times(1)).set(anyString(), anyString(), anyLong());
        verify(emailService, times(1)).sendCertificationEmail(anyString(), anyString());
    }

    @Test
    void sendCertificationEmail_학교미선택_예외발생() {
        // given
        member.updateSchool(null);
        CertificationRequest request = new CertificationRequest(localPart);

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
        CertificationRequest request = new CertificationRequest(localPart);

        // when & then
        assertThatThrownBy(() -> certificationService.sendCertificationEmail(member, request))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("학교 이메일만 인증 가능합니다.");
    }

    @Test
    void sendCertificationEmail_이미인증된이메일_예외발생() {
        // given
        String schoolEmail = localPart + "@" + school.getPostfix();
        member.updateSchoolEmail(schoolEmail);
        member.markAsVerified();
        CertificationRequest request = new CertificationRequest(localPart);

        given(emailUtil.getEmailAddress(localPart, school.getPostfix())).willReturn(schoolEmail);
        given(emailUtil.isSchoolEmail(schoolEmail)).willReturn(true);

        // when & then
        assertThatThrownBy(() -> certificationService.sendCertificationEmail(member, request))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("이미 인증된 이메일입니다.");
    }

    @Test
    void verifyCertification_인증하기_성공() {
        // given
        String schoolEmail = localPart + "@" + school.getPostfix();
        String key = "cert:" + member.getId() + ":" + schoolEmail;
        CertificationVerifyRequest request = new CertificationVerifyRequest(localPart, code);

        given(emailUtil.getEmailAddress(localPart, school.getPostfix())).willReturn(schoolEmail);
        given(emailUtil.getEmailCertificationKey(member.getId(), schoolEmail)).willReturn(
            "cert:" + member.getId() + ":" + schoolEmail);

        given(cache.get(key)).willReturn(code);

        doNothing().when(cache).delete(key);

        // when
        CertificationResponse response = certificationService.verifyCertification(member, request);

        // then
        assertThat(response.isVerified()).isTrue();
        assertThat(member.getIsVerified()).isTrue();
        assertThat(member.getSchoolEmail()).isEqualTo(schoolEmail);

        verify(cache, times(1)).delete(key);
    }

    @Test
    void verifyCertification_코드만료_예외발생() {
        String schoolEmail = localPart + "@" + school.getPostfix();
        String key = "cert:" + member.getId() + ":" + schoolEmail;
        CertificationVerifyRequest request = new CertificationVerifyRequest(localPart, code);

        given(emailUtil.getEmailAddress(localPart, school.getPostfix())).willReturn(schoolEmail);
        given(emailUtil.getEmailCertificationKey(member.getId(), schoolEmail)).willReturn(
            "cert:" + member.getId() + ":" + schoolEmail);

        given(cache.get(key)).willReturn(null);

        assertThatThrownBy(() -> certificationService.verifyCertification(member, request))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("인증 코드가 만료되었습니다.");
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
