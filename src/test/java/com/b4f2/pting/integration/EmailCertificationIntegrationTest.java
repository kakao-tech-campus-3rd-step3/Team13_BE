package com.b4f2.pting.integration;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.b4f2.pting.config.InMemoryCache;
import com.b4f2.pting.config.TestContainersConfig;
import com.b4f2.pting.domain.Member;
import com.b4f2.pting.domain.School;
import com.b4f2.pting.dto.CertificationRequest;
import com.b4f2.pting.dto.CertificationResponse;
import com.b4f2.pting.dto.CertificationVerifyRequest;
import com.b4f2.pting.dto.SchoolResponse;
import com.b4f2.pting.repository.MemberRepository;
import com.b4f2.pting.repository.SchoolRepository;
import com.b4f2.pting.service.EmailService;
import com.b4f2.pting.util.JwtUtil;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@Import(TestContainersConfig.class)
@SuppressWarnings("NonAsciiCharacters")
@Tag("integration")
class EmailCertificationIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private SchoolRepository schoolRepository;

    @Autowired
    private InMemoryCache cache;

    @MockitoBean
    private EmailService emailService;

    @Autowired
    private JwtUtil jwtUtil;

    private Member testMember;
    private School testSchool;
    private String token;

    @BeforeEach
    void setUp() {
        memberRepository.deleteAll();
        schoolRepository.deleteAll();

        testMember = memberRepository.save(new Member("12345", Member.OAuthProvider.KAKAO));
        token = jwtUtil.createToken(testMember);

        testSchool = schoolRepository.save(new School("부산대학교", "pusan.ac.kr"));
    }

    private HttpHeaders createAuthHeader() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    @Test
    @DisplayName("학교 선택 후 인증 이메일 발송")
    void selectSchoolAndSendEmail_성공() {
        // 학교 선택
        ResponseEntity<SchoolResponse> schoolResponse = restTemplate.exchange(
                "/api/v1/members/me/school/" + testSchool.getId(),
                HttpMethod.POST,
                new HttpEntity<>(createAuthHeader()),
                SchoolResponse.class);

        assertThat(schoolResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(schoolResponse.getBody().id()).isEqualTo(testSchool.getId());

        // 이메일 인증 요청
        CertificationRequest request = new CertificationRequest(testMember.getSchoolEmail());

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/v1/members/me/certification/email", new HttpEntity<>(request, createAuthHeader()), String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("인증 메일을 발송했습니다");
    }

    @Test
    @DisplayName("이메일 인증 코드 검증 성공")
    void verifyCertification_성공() {
        // given
        String localPart = "abc";
        String code = "123456";

        testMember.updateSchool(testSchool);
        ReflectionTestUtils.setField(testMember, "isVerified", false);
        memberRepository.save(testMember);

        String key = "cert:" + testMember.getId() + ":" + localPart + "@" + testSchool.getPostfix();
        cache.delete(key);
        cache.set(key, code, 5 * 60 * 1000);

        CertificationVerifyRequest request = new CertificationVerifyRequest(localPart, code);

        // when
        ResponseEntity<CertificationResponse> response = restTemplate.postForEntity(
                "/api/v1/members/me/certification/verify",
                new HttpEntity<>(request, createAuthHeader()),
                CertificationResponse.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().isVerified()).isTrue();

        Member refreshed = memberRepository.findById(testMember.getId()).orElseThrow();
        assertThat(refreshed.getIsVerified()).isTrue();
        assertThat(refreshed.getSchoolEmail()).isEqualTo(localPart + "@" + testSchool.getPostfix());
    }

    @Test
    @DisplayName("이메일 인증 상태 조회")
    void checkCertificationStatus_성공() {
        // given
        testMember.markAsVerified();
        memberRepository.save(testMember);

        // when
        ResponseEntity<CertificationResponse> response = restTemplate.exchange(
                "/api/v1/members/me/certification/status",
                HttpMethod.GET,
                new HttpEntity<>(createAuthHeader()),
                CertificationResponse.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().isVerified()).isTrue();
    }

    @Test
    @DisplayName("이메일 인증 코드 검증 실패")
    void verifyCertification_잘못된코드_예외발생() {
        // given
        String localPart = "abc";
        String correctCode = "123456";
        String wrongCode = "999999";

        String key = "cert:" + testMember.getId() + ":" + localPart + "@" + testSchool.getPostfix();
        cache.delete(key);
        cache.set(key, correctCode, 5 * 60 * 1000);

        CertificationVerifyRequest request = new CertificationVerifyRequest(localPart, wrongCode);

        // when
        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/v1/members/me/certification/verify", new HttpEntity<>(request, createAuthHeader()), String.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }
}
