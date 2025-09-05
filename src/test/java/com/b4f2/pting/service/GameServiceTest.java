package com.b4f2.pting.service;

import com.b4f2.pting.domain.Game;
import com.b4f2.pting.domain.Member;
import com.b4f2.pting.domain.Sport;
import com.b4f2.pting.dto.CreateGameRequest;
import com.b4f2.pting.dto.GameResponse;
import com.b4f2.pting.repository.GameRepository;
import com.b4f2.pting.repository.GameUserRepository;
import com.b4f2.pting.repository.SportRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GameServiceTest {

    @Mock
    private GameRepository gameRepository;

    @Mock
    private SportRepository sportRepository;

    @Mock
    private GameUserRepository gameUserRepository;

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

        game = new Game(sport, "재미있는 방", 10, Game.GameStatus.ON_MATCHING, LocalDateTime.now().plusHours(1), 2);
        ReflectionTestUtils.setField(game, "id", 1L);
    }

    @Test
    void 게임_생성_성공() {
        // given
        CreateGameRequest request = new CreateGameRequest(
                1L,
            "재미있는 방",
                10,
                ZonedDateTime.now(ZoneId.of("Asia/Seoul")).toLocalDateTime().plusHours(1),
                2
        );

        when(sportRepository.findById(request.sportId())).thenReturn(Optional.of(sport));
        when(gameRepository.save(any(Game.class))).thenReturn(game);

        // when
        GameResponse response = gameService.createGame(member, request);

        // then
        assertThat(response).isNotNull();
        assertThat(response.playerCount()).isEqualTo(request.playerCount());
        assertThat(response.startTime()).isEqualTo(request.startTime());

        verify(sportRepository).findById(request.sportId());
        verify(gameRepository).save(any(Game.class));
        verify(gameUserRepository).save(any());
    }

    @Test
    void 게임_참가_성공() {
        // given
        when(gameRepository.findById(game.getId())).thenReturn(Optional.of(game));
        when(gameUserRepository.existsByMemberIdAndGame(member.getId(), game)).thenReturn(false);
        when(gameUserRepository.countByGame(game)).thenReturn(5);

        // when
        gameService.joinGame(member, game.getId());

        // then
        verify(gameRepository).findById(game.getId());
        verify(gameUserRepository).existsByMemberIdAndGame(member.getId(), game);
        verify(gameUserRepository).countByGame(game);
        verify(gameUserRepository).save(any());
    }

    @Test
    void ID로_게임_조회_성공() {
        // given
        when(gameRepository.findById(game.getId())).thenReturn(Optional.of(game));

        // when
        GameResponse response = gameService.findGameById(game.getId());

        // then
        assertThat(response).isNotNull();
        assertThat(response.gameId()).isEqualTo(game.getId());

        verify(gameRepository).findById(game.getId());
    }
}
