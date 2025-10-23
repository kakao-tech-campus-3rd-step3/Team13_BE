package com.b4f2.pting.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class RankGameTest {

    private RankGame rankGame;
    private List<MatchResultVote> matchResultVoteList;

    @BeforeEach
    void setUp() {
        rankGame = new RankGame();
        matchResultVoteList = new ArrayList<>();
        ReflectionTestUtils.setField(rankGame, "matchResultVoteList", matchResultVoteList);
    }

    @Test
    void getWinTeam_투표가_없을_경우_NONE을_반환() {
        // when
        RankGameTeam winTeam = rankGame.getWinTeam();

        // then
        assertThat(winTeam).isEqualTo(RankGameTeam.NONE);
    }

    @Test
    void getWinTeam_레드팀_승리_조건_충족_시_RED_TEAM_반환() {
        // given
        // 60% 이상
        matchResultVoteList.add(new MatchResultVote(null, rankGame, RankGameTeam.RED_TEAM));
        matchResultVoteList.add(new MatchResultVote(null, rankGame, RankGameTeam.RED_TEAM));
        matchResultVoteList.add(new MatchResultVote(null, rankGame, RankGameTeam.RED_TEAM));
        matchResultVoteList.add(new MatchResultVote(null, rankGame, RankGameTeam.BLUE_TEAM));
        matchResultVoteList.add(new MatchResultVote(null, rankGame, RankGameTeam.BLUE_TEAM));

        // when
        RankGameTeam winTeam = rankGame.getWinTeam();

        // then
        assertThat(winTeam).isEqualTo(RankGameTeam.RED_TEAM);
    }

    @Test
    void getWinTeam_블루팀_승리_조건_충족_시_BLUE_TEAM_반환() {
        // given
        // 60% 이상
        matchResultVoteList.add(new MatchResultVote(null, rankGame, RankGameTeam.BLUE_TEAM));
        matchResultVoteList.add(new MatchResultVote(null, rankGame, RankGameTeam.BLUE_TEAM));
        matchResultVoteList.add(new MatchResultVote(null, rankGame, RankGameTeam.BLUE_TEAM));
        matchResultVoteList.add(new MatchResultVote(null, rankGame, RankGameTeam.RED_TEAM));
        matchResultVoteList.add(new MatchResultVote(null, rankGame, RankGameTeam.RED_TEAM));

        // when
        RankGameTeam winTeam = rankGame.getWinTeam();

        // then
        assertThat(winTeam).isEqualTo(RankGameTeam.BLUE_TEAM);
    }

    @Test
    void getWinTeam_승리_조건_미충족_시_NONE_반환() {
        // given
        // 50 vs 50
        matchResultVoteList.add(new MatchResultVote(null, rankGame, RankGameTeam.RED_TEAM));
        matchResultVoteList.add(new MatchResultVote(null, rankGame, RankGameTeam.BLUE_TEAM));

        // when
        RankGameTeam winTeam = rankGame.getWinTeam();

        // then
        assertThat(winTeam).isEqualTo(RankGameTeam.NONE);
    }

    @Test
    void hasMemberVote_멤버가_투표했을_경우_true_반환() {
        // given
        Member member = new Member();
        ReflectionTestUtils.setField(member, "id", 1L);
        matchResultVoteList.add(new MatchResultVote(member, rankGame, RankGameTeam.RED_TEAM));

        // when
        boolean hasVoted = rankGame.hasMemberVote(member);

        // then
        assertThat(hasVoted).isTrue();
    }

    @Test
    void hasMemberVote_멤버가_투표하지_않았을_경우_false_반환() {
        // given
        Member member1 = new Member();
        ReflectionTestUtils.setField(member1, "id", 1L);
        Member member2 = new Member();
        ReflectionTestUtils.setField(member2, "id", 2L);
        matchResultVoteList.add(new MatchResultVote(member1, rankGame, RankGameTeam.RED_TEAM));

        // when
        boolean hasVoted = rankGame.hasMemberVote(member2);

        // then
        assertThat(hasVoted).isFalse();
    }
}
