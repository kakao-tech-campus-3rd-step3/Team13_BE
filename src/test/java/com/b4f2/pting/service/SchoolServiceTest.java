package com.b4f2.pting.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import jakarta.persistence.EntityNotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.b4f2.pting.domain.Member;
import com.b4f2.pting.domain.School;
import com.b4f2.pting.dto.SchoolRequest;
import com.b4f2.pting.repository.SchoolRepository;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class SchoolServiceTest {

    @Mock
    private SchoolRepository schoolRepository;

    @InjectMocks
    private SchoolService schoolService;

    private School school;
    private Member member;

    @BeforeEach
    void setUp() {
        school = new School("부산대학교", "pusan.ac.kr");
        ReflectionTestUtils.setField(school, "id", 1L);

        member = new Member(1L, Member.OAuthProvider.KAKAO);
        ReflectionTestUtils.setField(member, "id", 1L);
    }

    @Test
    void getAllSchools_학교목록조회_성공() {
        // given
        when(schoolRepository.findAll()).thenReturn(List.of(school));

        // when
        List<School> schools = schoolService.getAllSchools();

        // then
        assertNotNull(schools);
        assertEquals(1, schools.size());
        assertEquals("부산대학교", schools.getFirst().getName());
        assertEquals("pusan.ac.kr", schools.getFirst().getPostfix());
        verify(schoolRepository, times(1)).findAll();
    }

    @Test
    void getSchoolById_학교상세조회_성공() {
        // given
        when(schoolRepository.findById(1L)).thenReturn(Optional.of(school));

        // when
        School response = schoolService.getSchoolById(1L);

        // then
        assertNotNull(response);
        assertEquals(school.getId(), response.getId());
        assertEquals(school.getName(), response.getName());
        assertEquals(school.getPostfix(), response.getPostfix());
        verify(schoolRepository, times(1)).findById(1L);
    }

    @Test
    void getSchoolById_학교없음_예외발생() {
        // given
        when(schoolRepository.findById(1L)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> schoolService.getSchoolById(1L))
            .isInstanceOf(EntityNotFoundException.class)
            .hasMessage("학교 정보가 존재하지 않습니다.");
        verify(schoolRepository, times(1)).findById(1L);
    }

    @Test
    void createSchool_학교등록_성공() {
        // given
        SchoolRequest request = new SchoolRequest("부산대학교", "pusan.ac.kr");
        School savedSchool = new School(request.name(), request.domain());
        ReflectionTestUtils.setField(savedSchool, "id", 1L);

        when(schoolRepository.save(ArgumentMatchers.any())).thenReturn(savedSchool);

        // when
        School response = schoolService.createSchool(request);

        // then
        assertNotNull(response);
        assertEquals(savedSchool.getId(), response.getId());
        assertEquals(savedSchool.getName(), response.getName());
        assertEquals(savedSchool.getPostfix(), response.getPostfix());

        verify(schoolRepository, times(1)).save(ArgumentMatchers.any());
    }

    @Test
    void selectSchool_학교없음_예외발생() {
        // given
        when(schoolRepository.findById(1L)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> schoolService.selectSchool(member, 1L))
            .isInstanceOf(EntityNotFoundException.class)
            .hasMessage("학교 정보가 존재하지 않습니다.");
        verify(schoolRepository, times(1)).findById(1L);
    }
}
