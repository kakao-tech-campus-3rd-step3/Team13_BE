package com.b4f2.pting.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import jakarta.persistence.EntityNotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.b4f2.pting.domain.Member;
import com.b4f2.pting.domain.Member.OAuthProvider;
import com.b4f2.pting.domain.School;
import com.b4f2.pting.repository.MemberRepository;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private MemberService memberService;

    private Member member;
    private School school;

    @BeforeEach
    void setUp() {
        member = new Member("1", OAuthProvider.KAKAO);
        ReflectionTestUtils.setField(member, "id", 1L);

        school = new School("부산대학교", "pusan.ac.kr");
        ReflectionTestUtils.setField(school, "id", 1L);
    }

    @Test
    void getMemberById_회원조회_성공() {
        // given
        when(memberRepository.findById(member.getId())).thenReturn(Optional.of(member));

        // when
        Member foundMember = memberService.getMemberById(member.getId());

        // then
        assertNotNull(foundMember);
        assertEquals(member.getId(), foundMember.getId());
        verify(memberRepository, times(1)).findById(member.getId());
    }

    @Test
    void getMemberById_회원없음_예외발생() {
        // given
        when(memberRepository.findById(member.getId())).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> memberService.getMemberById(member.getId()))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("회원 정보가 존재하지 않습니다.");
        verify(memberRepository, times(1)).findById(member.getId());
    }
}
