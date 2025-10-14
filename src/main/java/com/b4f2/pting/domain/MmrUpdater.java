package com.b4f2.pting.domain;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import com.pocketcombats.openskill.Adjudicator;
import com.pocketcombats.openskill.RatingModelConfig;
import com.pocketcombats.openskill.data.RatingAdjustment;
import com.pocketcombats.openskill.data.SimplePlayerResult;
import com.pocketcombats.openskill.data.SimpleTeamResult;
import com.pocketcombats.openskill.model.BradleyTerryFull;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MmrUpdater {

    private static final RatingModelConfig config = RatingModelConfig.builder()
            // 핵심 매개변수 (Core Parameters)
            .setBeta(25.0 / 6.0) // 능력치 불확실성(sigma)의 스케일링 계수
            .setKappa(0.0001) // 능력치 업데이트 감쇠 계수
            .setTau(25.0 / 300.0) // 동적 불확실성 조정 계수
            // 동작 제어 (Behavioral Controls)
            .setLimitSigma(false) // true면 sigma 성장 제한
            .setBalance(true) // 팀 능력치 집계 시 균형 적용
            // 균형 조정 (Balance tuning)
            .setZ(3.0) // 균형 계산에 사용되는 Z-점수
            .setAlpha(1.0) // 균형 민감도
            .setTarget(0.0) // 목표 평균 차이
            .build();

    private static final Adjudicator<Long> adjudicator = new Adjudicator<>(config, new BradleyTerryFull(config));

    private final List<Mmr> winMmrList;
    private final List<Mmr> lossMmrList;

    public void update() {
        SimpleTeamResult<Long> winTeam = getTeamResult(winMmrList, true);
        SimpleTeamResult<Long> lossTeam = getTeamResult(lossMmrList, false);

        List<RatingAdjustment<Long>> adjustments = adjudicator.rate(List.of(winTeam, lossTeam));

        adjustments.forEach(adjustment -> {
            findMmrById(adjustment.playerId()).ifPresent(mmr -> {
                mmr.updateMu(adjustment.mu());
                mmr.updateSigma(adjustment.sigma());
            });
        });
    }

    private Optional<Mmr> findMmrById(Long id) {
        return Stream.concat(winMmrList.stream(), lossMmrList.stream())
                .filter(mmr -> mmr.isId(id))
                .findFirst();
    }

    private SimpleTeamResult<Long> getTeamResult(List<Mmr> mmrList, boolean isWinner) {
        List<SimplePlayerResult<Long>> playerResults =
                mmrList.stream().map(Mmr::getSimplePlayerResult).toList();

        double mu = playerResults.stream().mapToDouble(SimplePlayerResult::mu).sum() / playerResults.size();
        double sigma =
                playerResults.stream().mapToDouble(SimplePlayerResult::sigma).sum() / playerResults.size();

        return new SimpleTeamResult<Long>(mu, sigma, isWinner ? 1 : 2, playerResults);
    }
}
