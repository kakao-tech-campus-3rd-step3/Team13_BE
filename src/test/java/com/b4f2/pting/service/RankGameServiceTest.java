package com.b4f2.pting.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.b4f2.pting.domain.Game;
import com.b4f2.pting.domain.Game.GameStatus;
import com.b4f2.pting.domain.GameParticipant;
import com.b4f2.pting.domain.Member;
import com.b4f2.pting.domain.RankGame;
import com.b4f2.pting.domain.RankGameTeam;
import com.b4f2.pting.dto.VoteRequest;
import com.b4f2.pting.dto.VoteResultResponse;
import com.b4f2.pting.repository.GameParticipantRepository;
import com.b4f2.pting.repository.MatchResultVoteRepository;
import com.b4f2.pting.repository.MemberRepository;
import com.b4f2.pting.repository.MmrRepository;
import com.b4f2.pting.repository.RankGameParticipantRepository;
import com.b4f2.pting.repository.RankGameRepository;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class RankGameServiceTest {

    @Mock
    private RankGameParticipantRepository rankGameParticipantRepository;
    @Mock
    private GameParticipantRepository gameParticipantRepository;
    @Mock
    private RankGameRepository rankGameRepository;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private MatchResultVoteRepository matchResultVoteRepository;
    @Mock
    private MmrRepository mmrRepository;

    private RankGameService rankGameService;

    private Member member;
    private RankGame endedGame;
    private RankGame ongoingGame;
    private final Long gameId = 1L;
    private final Long memberId = 1L;

    @BeforeEach
    void setUp() {
        rankGameService = spy(new RankGameService(rankGameParticipantRepository, gameParticipantRepository, rankGameRepository, memberRepository, matchResultVoteRepository, mmrRepository));

        member = new Member(1L, Member.OAuthProvider.KAKAO);
        ReflectionTestUtils.setField(member, "id", memberId);

        endedGame = new RankGame();
        ReflectionTestUtils.setField(endedGame, "id", gameId);
        ReflectionTestUtils.setField(endedGame, "gameStatus", GameStatus.END);

        ongoingGame = new RankGame();
        ReflectionTestUtils.setField(ongoingGame, "id", gameId);
        ReflectionTestUtils.setField(ongoingGame, "gameStatus", GameStatus.ON_MATCHING);
    }

    @Test
    void voteMatchResult_투표_성공() {
        // given
        VoteRequest request = new VoteRequest(RankGameTeam.RED_TEAM);
        when(rankGameRepository.findById(gameId)).thenReturn(Optional.of(endedGame));
        when(gameParticipantRepository.findByGame(endedGame)).thenReturn(Collections.emptyList());

        // when
        VoteResultResponse response = rankGameService.voteMatchResult(gameId, request, member);

        // then
        assertThat(response.votedTeams()).hasSize(1);
        assertThat(response.votedTeams().get(0)).isEqualTo(RankGameTeam.RED_TEAM);
        verify(matchResultVoteRepository).save(any());
    }

    @Test
    void voteMatchResult_게임을_찾을_수_없으면_예외_발생() {
        // given
        VoteRequest request = new VoteRequest(RankGameTeam.RED_TEAM);
        when(rankGameRepository.findById(gameId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> rankGameService.voteMatchResult(gameId, request, member))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("게임을 찾을 수 없습니다.");
    }

    @Test
    void voteMatchResult_종료되지_않은_게임에_투표하면_예외_발생() {
        // given
        VoteRequest request = new VoteRequest(RankGameTeam.RED_TEAM);
        when(rankGameRepository.findById(gameId)).thenReturn(Optional.of(ongoingGame));

        // when & then
        assertThatThrownBy(() -> rankGameService.voteMatchResult(gameId, request, member))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("아직 게임이 끝나지 않았습니다.");
    }

    @Test
    void voteMatchResult_이미_투표한_경우_예외_발생() {
        // given
        VoteRequest request = new VoteRequest(RankGameTeam.RED_TEAM);
        endedGame.vote(new com.b4f2.pting.domain.MatchResultVote(member, endedGame, RankGameTeam.BLUE_TEAM));
        when(rankGameRepository.findById(gameId)).thenReturn(Optional.of(endedGame));

        // when & then
        assertThatThrownBy(() -> rankGameService.voteMatchResult(gameId, request, member))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("이미 투표를 완료했습니다.");
    }

    @Test
    void checkAndHandle_투표가_모두_완료되면_handleVoteResult_호출() {
        // given
        doNothing().when(rankGameService).handleVoteResult(any(RankGame.class));

        VoteRequest request = new VoteRequest(RankGameTeam.RED_TEAM);
        GameParticipant participant = new GameParticipant(member, endedGame);
        when(rankGameRepository.findById(gameId)).thenReturn(Optional.of(endedGame));
        when(gameParticipantRepository.findByGame(endedGame)).thenReturn(List.of(participant));

        // when
        rankGameService.voteMatchResult(gameId, request, member);

        // then
        verify(rankGameService, times(1)).handleVoteResult(endedGame);
    }
}
