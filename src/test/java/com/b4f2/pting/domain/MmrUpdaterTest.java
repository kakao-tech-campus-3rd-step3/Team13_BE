package com.b4f2.pting.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class MmrUpdaterTest {

    private Member winner;
    private Member loser;
    private Sport sport;

    @BeforeEach
    void setUp() {
        winner = new Member("1", Member.OAuthProvider.KAKAO);
        ReflectionTestUtils.setField(winner, "id", 1L);

        loser = new Member("2", Member.OAuthProvider.KAKAO);
        ReflectionTestUtils.setField(loser, "id", 2L);

        sport = new Sport("테스트 운동", 2);
        ReflectionTestUtils.setField(sport, "id", 1L);
    }

    @Test
    @DisplayName("MMR 업데이트 테스트: 승자는 mu가 증가하고, 패자는 mu가 감소한다.")
    void testUpdateMmr() {
        // given
        Mmr winnerMmr = new Mmr(1L, sport, winner, 25.0, 8.3);
        Mmr loserMmr = new Mmr(2L, sport, loser, 25.0, 8.3);

        List<Mmr> winMmrList = List.of(winnerMmr);
        List<Mmr> lossMmrList = List.of(loserMmr);

        MmrUpdater mmrUpdater = new MmrUpdater(winMmrList, lossMmrList);

        // when
        mmrUpdater.update();

        // then
        assertThat(winnerMmr.getMu()).isGreaterThan(25.0);
        assertThat(loserMmr.getMu()).isLessThan(25.0);
    }
}
