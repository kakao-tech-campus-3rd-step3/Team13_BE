package com.b4f2.pting.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.b4f2.pting.domain.Sport;
import com.b4f2.pting.dto.SportsResponse;
import com.b4f2.pting.repository.SportRepository;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class SportServiceTest {

    @Mock
    private SportRepository sportRepository;

    @InjectMocks
    private SportService sportService;

    @Test
    void findAllSports_모든스포츠목록조회_성공() {
        // given
        Sport football = new Sport();
        ReflectionTestUtils.setField(football, "id", 1L);
        ReflectionTestUtils.setField(football, "name", "축구");

        Sport basketball = new Sport();
        ReflectionTestUtils.setField(basketball, "id", 2L);
        ReflectionTestUtils.setField(basketball, "name", "농구");

        List<Sport> sports = Arrays.asList(football, basketball);

        when(sportRepository.findAll()).thenReturn(sports);

        // when
        SportsResponse response = sportService.findAllSports();

        // then
        assertThat(response).isNotNull();
        assertThat(response.sports()).hasSize(2);
        assertThat(response.sports().get(0).name()).isEqualTo("축구");
        assertThat(response.sports().get(1).name()).isEqualTo("농구");
    }

    @Test
    void findAllSports_스포츠목록이없을때_빈목록조회() {
        // given
        when(sportRepository.findAll()).thenReturn(Collections.emptyList());

        // when
        SportsResponse response = sportService.findAllSports();

        // then
        assertThat(response).isNotNull();
        assertThat(response.sports()).isEmpty();
    }
}
