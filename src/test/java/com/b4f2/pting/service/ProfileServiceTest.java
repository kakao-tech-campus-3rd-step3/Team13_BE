package com.b4f2.pting.service;

import com.b4f2.pting.domain.Member;
import com.b4f2.pting.domain.Member.OAuthProvider;
import com.b4f2.pting.dto.ProfileResponse;
import com.b4f2.pting.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProfileServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private ProfileService profileService;

    private Member member;

    @BeforeEach
    void setUp() {
        member = new Member(1L, OAuthProvider.KAKAO);
        ReflectionTestUtils.setField(member, "schoolEmail", "test@example.com");
        ReflectionTestUtils.setField(member, "id", 1L);
    }

    @Test
    void getProfile_프로필조회_성공() {
        // given
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));

        // when
        ProfileResponse response = profileService.getProfile(1L);

        // then
        assertThat(response.email()).isEqualTo(member.getSchoolEmail());
        verify(memberRepository).findById(1L);
    }

    @Test
    void getProfile_존재하지않는프로필조회_예외발생() {
        // given
        when(memberRepository.findById(anyLong())).thenReturn(Optional.empty());

        // when, then
        assertThatThrownBy(() -> profileService.getProfile(1L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("해당 유저는 존재하지 않습니다.");
    }

    @Test
    void updateName_이름변경_성공() {
        // given
        String newName = "newName";
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));

        // when
        ProfileResponse response = profileService.updateName(1L, newName);

        // then
        assertThat(response.name()).isEqualTo(newName);
        assertThat(member.getName()).isEqualTo(newName);
        verify(memberRepository).findById(1L);
    }

    @Test
    void updateDescription_설명변경_성공() {
        // given
        String newDescription = "newDescription";
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));

        // when
        ProfileResponse response = profileService.updateDescription(1L, newDescription);

        // then
        assertThat(response.description()).isEqualTo(newDescription);
        assertThat(member.getDescription()).isEqualTo(newDescription);
        verify(memberRepository).findById(1L);
    }

    @Test
    void updateImageUrl_이미지URL변경_성공() {
        // given
        String newImageUrl = "newImageUrl";
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));

        // when
        ProfileResponse response = profileService.updateImageUrl(1L, newImageUrl);

        // then
        assertThat(response.imageUrl()).isEqualTo(newImageUrl);
        assertThat(member.getImageUrl()).isEqualTo(newImageUrl);
        verify(memberRepository).findById(1L);
    }
}
