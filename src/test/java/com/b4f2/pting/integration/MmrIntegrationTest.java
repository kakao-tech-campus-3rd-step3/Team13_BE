package com.b4f2.pting.integration;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.b4f2.pting.config.TestContainersConfig;
import com.b4f2.pting.domain.Game.GameStatus;
import com.b4f2.pting.domain.Member;
import com.b4f2.pting.domain.Mmr;
import com.b4f2.pting.domain.RankGame;
import com.b4f2.pting.domain.RankGameParticipant;
import com.b4f2.pting.domain.RankGameTeam;
import com.b4f2.pting.domain.Sport;
import com.b4f2.pting.dto.VoteRequest;
import com.b4f2.pting.repository.MemberRepository;
import com.b4f2.pting.repository.MmrRepository;
import com.b4f2.pting.repository.RankGameParticipantRepository;
import com.b4f2.pting.repository.RankGameRepository;
import com.b4f2.pting.repository.SportRepository;
import com.b4f2.pting.service.RankGameService;
import com.b4f2.pting.util.KakaoOAuthClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@Import(TestContainersConfig.class)
@Tag("integration")
class MmrIntegrationTest {

    @Autowired
    private RankGameService rankGameService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private SportRepository sportRepository;

    @Autowired
    private MmrRepository mmrRepository;

    @Autowired
    private RankGameRepository rankGameRepository;

    @Autowired
    private RankGameParticipantRepository rankGameParticipantRepository;

    @MockitoBean
    private KakaoOAuthClient kakaoOAuthClient;

    @MockitoBean
    private JavaMailSender javaMailSender;

    private Member winner;
    private Member loser;
    private Sport sport;
    private RankGame game;

    @BeforeEach
    void setUp() {
        sport = sportRepository.save(new Sport("축구", 11));

        winner = memberRepository.save(new Member("1", Member.OAuthProvider.KAKAO));
        loser = memberRepository.save(new Member("2", Member.OAuthProvider.KAKAO));

        mmrRepository.save(new Mmr(sport, winner));
        mmrRepository.save(new Mmr(sport, loser));

        RankGame game = new RankGame();
        org.springframework.test.util.ReflectionTestUtils.setField(game, "sport", sport);
        org.springframework.test.util.ReflectionTestUtils.setField(game, "playerCount", 2);
        org.springframework.test.util.ReflectionTestUtils.setField(game, "gameStatus", GameStatus.END);
        org.springframework.test.util.ReflectionTestUtils.setField(
                game, "startTime", LocalDateTime.now().minusHours(1));
        org.springframework.test.util.ReflectionTestUtils.setField(game, "duration", 60);
        org.springframework.test.util.ReflectionTestUtils.setField(game, "description", "테스트용 게임");
        this.game = rankGameRepository.save(game);

        RankGameParticipant winnerParticipant = new RankGameParticipant();
        org.springframework.test.util.ReflectionTestUtils.setField(winnerParticipant, "member", winner);
        org.springframework.test.util.ReflectionTestUtils.setField(winnerParticipant, "game", this.game);
        org.springframework.test.util.ReflectionTestUtils.setField(winnerParticipant, "team", RankGameTeam.RED_TEAM);
        rankGameParticipantRepository.save(winnerParticipant);

        RankGameParticipant loserParticipant = new RankGameParticipant();
        org.springframework.test.util.ReflectionTestUtils.setField(loserParticipant, "member", loser);
        org.springframework.test.util.ReflectionTestUtils.setField(loserParticipant, "game", this.game);
        org.springframework.test.util.ReflectionTestUtils.setField(loserParticipant, "team", RankGameTeam.BLUE_TEAM);
        rankGameParticipantRepository.save(loserParticipant);
    }

    @Test
    @DisplayName("경기 결과 투표 완료 시 MMR이 정상적으로 업데이트되고 데이터베이스에 반영된다.")
    void testMmrUpdateOnVoteCompletion() {
        // given
        double initialWinnerMu =
                mmrRepository.findByMemberAndSport(winner, sport).get().getMu();
        double initialLoserMu =
                mmrRepository.findByMemberAndSport(loser, sport).get().getMu();

        VoteRequest voteRequest = new VoteRequest(RankGameTeam.RED_TEAM);

        // when
        // 모든 참가자가 투표 (RED 팀 승리)
        rankGameService.voteMatchResult(game.getId(), voteRequest, winner);
        rankGameService.voteMatchResult(game.getId(), voteRequest, loser);

        // then
        Mmr updatedWinnerMmr = mmrRepository.findByMemberAndSport(winner, sport).get();
        Mmr updatedLoserMmr = mmrRepository.findByMemberAndSport(loser, sport).get();

        assertThat(updatedWinnerMmr.getMu()).isGreaterThan(initialWinnerMu);
        assertThat(updatedLoserMmr.getMu()).isLessThan(initialLoserMu);
    }
}
