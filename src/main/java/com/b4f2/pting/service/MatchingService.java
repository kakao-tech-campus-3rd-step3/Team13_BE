package com.b4f2.pting.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.b4f2.pting.algorithm.MatchingAlgorithm;
import com.b4f2.pting.domain.Game.GameStatus;
import com.b4f2.pting.domain.MatchingQueue;
import com.b4f2.pting.domain.RankGame;
import com.b4f2.pting.domain.RankGameParticipant;
import com.b4f2.pting.domain.RankGameParticipants;
import com.b4f2.pting.domain.RankGameTeam;
import com.b4f2.pting.domain.Sport;
import com.b4f2.pting.repository.RankGameParticipantRepository;
import com.b4f2.pting.repository.RankGameRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MatchingService {

    private final MatchingQueue matchingQueue;
    private final RankGameRepository rankGameRepository;
    private final RankGameParticipantRepository rankGameParticipantRepository;
    private final MatchingAlgorithm matchingAlgorithm;

    @Transactional
    public void addPlayerToQueue(Sport sport, RankGameParticipant participant) {
        matchingQueue.addPlayer(sport.getId(), participant);
    }

    @Transactional
    public List<RankGame> proposeGamesFromQueue(Sport sport) {
        RankGameParticipants queue = matchingQueue.getPlayers(sport.getId());
        List<RankGameParticipant> participants = queue.getGameParticipantList();

        if (participants.size() < sport.getRecommendedPlayerCount()) { // 최소 인원 미달(경기에 필요한 인원)
            return Collections.emptyList();
        }

        List<List<RankGameParticipant>> matchedTeam = matchingAlgorithm.match(participants, sport);

        List<RankGame> proposeGames = new ArrayList<>();

        for (List<RankGameParticipant> team : matchedTeam) {
            LocalDateTime startTime = getNextSaturdayGameTime();

            RankGame game = RankGame.create(
                sport,
                "랭크 게임",
                team.size(),
                GameStatus.ON_RECRUITING,
                startTime,
                60,
                "자동 생성된 랭크 게임 (확정 전)"
            );

            team.sort((p1, p2) ->
                Double.compare(p2.getMember().getMmr(sport), p1.getMember().getMmr(sport))
            );

            for (int i = 0; i < team.size(); i++) {
                RankGameParticipant participant = team.get(i);
                participant.assignGame(game);

                participant.assignTeam(i % 2 == 0 ? RankGameTeam.RED_TEAM : RankGameTeam.BLUE_TEAM);
            }

            // TODO: 참가자에게 팀 + 일정 안내 (푸시/알림)

            proposeGames.add(game);
        }

        return proposeGames;
    }

    @Transactional
    public void acceptTeam(RankGameParticipant participant) {
        participant.accept();
        rankGameParticipantRepository.save(participant);

        RankGameParticipants participants = new RankGameParticipants(
            rankGameParticipantRepository.findAllByGame(participant.getGame())
        );

        if (participants.getGameParticipantList().stream().allMatch(RankGameParticipant::isAccepted)) {
            RankGame game = (RankGame) participant.getGame();
            game.changeStatus(GameStatus.FULL);
            rankGameRepository.save(game);
            rankGameParticipantRepository.saveAll(participants.getGameParticipantList());
            matchingQueue.removePlayers(game.getSport().getId(), participants.getGameParticipantList());
        }
    }

    // 오늘 이후에 오는 토요일 15시
    private LocalDateTime getNextSaturdayGameTime() {
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));
        LocalDate nextSaturday = today.with(TemporalAdjusters.next(java.time.DayOfWeek.SATURDAY));
        return LocalDateTime.of(nextSaturday, LocalTime.of(15, 0));
    }
}
