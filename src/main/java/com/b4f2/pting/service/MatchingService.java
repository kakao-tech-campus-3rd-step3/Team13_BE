package com.b4f2.pting.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.firebase.messaging.FirebaseMessagingException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.b4f2.pting.algorithm.MatchingAlgorithm;
import com.b4f2.pting.domain.FcmToken;
import com.b4f2.pting.domain.Game.GameStatus;
import com.b4f2.pting.domain.MatchingQueue;
import com.b4f2.pting.domain.Member;
import com.b4f2.pting.domain.RankGame;
import com.b4f2.pting.domain.RankGameParticipant;
import com.b4f2.pting.domain.RankGameParticipants;
import com.b4f2.pting.domain.RankGameTeam;
import com.b4f2.pting.domain.Sport;
import com.b4f2.pting.dto.RankGameConfirmRequest;
import com.b4f2.pting.dto.RankGameEnqueueRequest;
import com.b4f2.pting.repository.FcmTokenRepository;
import com.b4f2.pting.repository.RankGameParticipantRepository;
import com.b4f2.pting.repository.RankGameRepository;
import com.b4f2.pting.repository.SportRepository;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MatchingService {

    @Value("${app.default-image-url}")
    private String defaultImageUrl;

    private final MatchingQueue matchingQueue;
    private final RankGameRepository rankGameRepository;
    private final RankGameParticipantRepository rankGameParticipantRepository;
    private final MatchingAlgorithm matchingAlgorithm;
    private final FcmService fcmService;
    private final FcmTokenRepository fcmTokenRepository;
    private final SportRepository sportRepository;

    @Transactional
    public RankGameParticipant addPlayerToQueue(Member member, RankGameEnqueueRequest request) {

        boolean exists = sportRepository.existsById(request.sportId());
        if (!exists) {
            throw new EntityNotFoundException("해당 스포츠가 존재하지 않습니다.");
        }

        RankGameParticipant participant = new RankGameParticipant(member);

        matchingQueue.addPlayer(request.sportId(), participant);

        return rankGameParticipantRepository.save(participant);
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
                    "장소",
                    team.size(),
                    GameStatus.ON_RECRUITING,
                    startTime,
                    60,
                    "자동 생성된 랭크 게임 (확정 전)",
                    defaultImageUrl);

            team.sort((p1, p2) ->
                    Double.compare(p2.getMember().getMmr(sport), p1.getMember().getMmr(sport)));

            for (int i = 0; i < team.size(); i++) {
                RankGameParticipant participant = team.get(i);
                participant.assignGame(game);

                participant.assignTeam(i % 2 == 0 ? RankGameTeam.RED_TEAM : RankGameTeam.BLUE_TEAM);
            }

            // 참가자에게 팀 + 일정 안내 (푸시/알림)
            try {
                notifyTeamSchedule(team, game);
            } catch (FirebaseMessagingException e) {
                log.error("매칭 완료 푸시 알림 전송 실패", e);
            }

            proposeGames.add(game);
        }

        return proposeGames;
    }

    private void notifyTeamSchedule(List<RankGameParticipant> participants, RankGame game)
            throws FirebaseMessagingException {
        List<Member> members =
                participants.stream().map(RankGameParticipant::getMember).toList();

        List<String> tokens = fcmTokenRepository.findAllByMemberIn(members).stream()
                .map(FcmToken::getToken)
                .filter(Objects::nonNull)
                .toList();

        if (tokens.isEmpty()) return;

        String title = "[랭크 게임] 일정 안내";
        String body = game.getName() + "이 매칭되었습니다!\n" + "시작 시간: "
                + game.getStartTime().format(DateTimeFormatter.ofPattern("MM/dd HH:mm")) + "\n"
                + "⚠️ 참여자 여러분, 일정을 확인하고 참가를 확정해주세요!";

        fcmService.sendMulticastPush(tokens, title, body);
    }

    @Transactional
    public void acceptTeam(Member member, RankGameConfirmRequest request) {
        RankGameParticipant participant = rankGameParticipantRepository
                .findByGameIdAndMemberId(request.rankGameId(), member.getId())
                .orElseThrow(() -> new IllegalArgumentException("참가자를 찾을 수 없습니다."));

        RankGame game = rankGameRepository
                .findById(request.rankGameId())
                .orElseThrow(() -> new IllegalArgumentException("게임을 찾을 수 없습니다."));

        participant.accept();
        rankGameParticipantRepository.save(participant);

        RankGameParticipants participants = new RankGameParticipants(rankGameParticipantRepository.findAllByGame(game));

        // 랭크 게임 확정
        if (participants.getGameParticipantList().stream().allMatch(RankGameParticipant::isAccepted)) {
            game.changeStatus(GameStatus.FULL);
            rankGameRepository.save(game);
            rankGameParticipantRepository.saveAll(participants.getGameParticipantList());
            matchingQueue.removePlayers(game.getSport().getId(), participants.getGameParticipantList());

            // 푸시 알림
            try {
                notifyMatchingCompleted(participants.getGameParticipantList(), game);
            } catch (FirebaseMessagingException e) {
                log.error("매칭 완료 푸시 알림 전송 실패", e);
            }
        }
    }

    private void notifyMatchingCompleted(List<RankGameParticipant> participants, RankGame game)
            throws FirebaseMessagingException {
        List<Member> members =
                participants.stream().map(RankGameParticipant::getMember).toList();

        List<String> tokens = fcmTokenRepository.findAllByMemberIn(members).stream()
                .map(FcmToken::getToken)
                .toList();

        if (tokens.isEmpty()) return;

        String title = "[랭크 게임] 매칭 완료";
        String body = game.getName() + "이 매칭되었습니다!\n" + "시작 시간: "
                + game.getStartTime().format(DateTimeFormatter.ofPattern("MM/dd HH:mm")) + "\n" + "팀: "
                + participants.stream()
                        .map(p -> p.getMember().getName() + "(" + p.getTeam().name() + ")")
                        .collect(Collectors.joining(", "));

        fcmService.sendMulticastPush(tokens, title, body);
    }

    // 오늘 이후에 오는 토요일 15시
    private LocalDateTime getNextSaturdayGameTime() {
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));
        LocalDate nextSaturday = today.with(TemporalAdjusters.next(java.time.DayOfWeek.SATURDAY));
        return LocalDateTime.of(nextSaturday, LocalTime.of(15, 0));
    }
}
