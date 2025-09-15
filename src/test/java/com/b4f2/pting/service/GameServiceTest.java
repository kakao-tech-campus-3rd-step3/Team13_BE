package com.b4f2.pting.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.b4f2.pting.domain.Game;
import com.b4f2.pting.domain.Member;
import com.b4f2.pting.domain.Sport;
import com.b4f2.pting.dto.CreateGameRequest;
import com.b4f2.pting.dto.GameDetailResponse;
import com.b4f2.pting.repository.GameParticipantRepository;
import com.b4f2.pting.repository.GameRepository;
import com.b4f2.pting.repository.SportRepository;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class GameServiceTest {

    @Mock
    private GameRepository gameRepository;

    @Mock
    private SportRepository sportRepository;

    @Mock
    private GameParticipantRepository gameParticipantRepository;

    @InjectMocks
    private GameService gameService;

    private Member member;
    private Sport sport;
    private Game game;

    @BeforeEach
    void setUp() {
        member = new Member(1L, Member.OAuthProvider.KAKAO);
        ReflectionTestUtils.setField(member, "id", 1L);
        ReflectionTestUtils.setField(member, "isVerified", true);

        sport = new Sport();
        ReflectionTestUtils.setField(sport, "id", 1L);
        ReflectionTestUtils.setField(sport, "name", "축구");

        game = new Game(
            sport,
            "재미있는 방",
            10,
            Game.GameStatus.ON_MATCHING,
            LocalDateTime.now().plusHours(1),
            2,
            "재미있는 방 설명입니다."
        );
        ReflectionTestUtils.setField(game, "id", 1L);
    }

    @Test
    void createGame_게임생성_성공() {
        // given
        CreateGameRequest request = new CreateGameRequest(
            1L,
            "재미있는 방",
            10,
            LocalDateTime.now(ZoneId.of("Asia/Seoul")).plusHours(1),
            2,
            "재미있는 방 설명입니다."
        );

        when(sportRepository.findById(request.sportId())).thenReturn(Optional.of(sport));
        when(gameRepository.save(any(Game.class))).thenReturn(game);

        // when
        GameDetailResponse response = gameService.createGame(member, request);

        // then
        assertThat(response).isNotNull();
        assertThat(response.playerCount()).isEqualTo(request.playerCount());
        assertThat(response.startTime()).isEqualTo(request.startTime());

        verify(sportRepository).findById(request.sportId());
        verify(gameRepository).save(any(Game.class));
        verify(gameParticipantRepository).save(any());
    }

    @Test
    void joinGame_게임참가_성공() {
        // given
        when(gameRepository.findById(game.getId())).thenReturn(Optional.of(game));
        when(gameParticipantRepository.findByGame(game)).thenReturn(List.of());

        // when
        gameService.joinGame(member, game.getId());

        // then
        verify(gameParticipantRepository).save(argThat(gp ->
            gp.getGame().equals(game) && gp.getMember().equals(member)
        ));
    }

    @Test
    void findGameById_ID로게임조회_성공() {
        // given
        when(gameRepository.findById(game.getId())).thenReturn(Optional.of(game));

        // when
        GameDetailResponse response = gameService.findGameById(game.getId());

        // then
        assertThat(response).isNotNull();
        assertThat(response.gameId()).isEqualTo(game.getId());

        verify(gameRepository).findById(game.getId());
    }
}
