package com.b4f2.pting.service;

import java.time.LocalDateTime;
import java.util.List;

import javax.annotation.Nullable;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.google.firebase.messaging.FirebaseMessagingException;

import lombok.RequiredArgsConstructor;

import com.b4f2.pting.domain.FcmToken;
import com.b4f2.pting.domain.Game;
import com.b4f2.pting.domain.Game.GameStatus;
import com.b4f2.pting.domain.GameParticipant;
import com.b4f2.pting.domain.GameParticipants;
import com.b4f2.pting.domain.Member;
import com.b4f2.pting.domain.Sport;
import com.b4f2.pting.domain.TimePeriod;
import com.b4f2.pting.dto.CreateGameRequest;
import com.b4f2.pting.dto.GameDetailResponse;
import com.b4f2.pting.dto.GameResponse;
import com.b4f2.pting.dto.GamesResponse;
import com.b4f2.pting.repository.FcmTokenRepository;
import com.b4f2.pting.repository.GameParticipantRepository;
import com.b4f2.pting.repository.GameRepository;
import com.b4f2.pting.repository.SportRepository;
import com.b4f2.pting.repository.projection.ClosedGameSummary;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GameService {

    private final GameRepository gameRepository;
    private final GameParticipantRepository gameParticipantRepository;
    private final SportRepository sportRepository;
    private final FcmService fcmService;
    private final FcmTokenRepository fcmTokenRepository;
    private final S3UploadService s3UploadService;

    @Transactional
    public GameDetailResponse createGame(Member member, CreateGameRequest request, @Nullable MultipartFile image) {
        validateMemberIsVerified(member);

        Sport sport = sportRepository
                .findById(request.sportId())
                .orElseThrow(() -> new EntityNotFoundException("해당 스포츠가 존재하지 않습니다."));

        String imageUrl = s3UploadService.saveImage(image);

        Game game = Game.create(
                sport,
                request.name(),
                request.gameLocation(),
                request.playerCount(),
                Game.GameStatus.ON_RECRUITING,
                request.startTime(),
                request.duration(),
                request.description(),
                imageUrl);

        gameRepository.save(game);

        addParticipant(game, member);

        return new GameDetailResponse(game, 1);
    }

    @Transactional
    public void joinGame(Member member, Long gameId) throws FirebaseMessagingException {
        validateMemberIsVerified(member);

        Game game = gameRepository.findById(gameId).orElseThrow(() -> new EntityNotFoundException("해당 게임이 없습니다."));

        if (game.getGameStatus() != Game.GameStatus.ON_RECRUITING) {
            throw new IllegalStateException("게임이 모집 중이 아닙니다.");
        }

        addParticipant(game, member);

        List<GameParticipant> participants = gameParticipantRepository.findByGame(game);
        if (participants.size() == game.getPlayerCount()) {
            game.changeStatus(Game.GameStatus.FULL);
            notifyMatchingCompleted(participants);
        }
    }

    @Transactional
    public List<ClosedGameSummary> endMatchingGames(LocalDateTime deadLine) {
        return gameRepository.endMatchingGames(deadLine);
    }

    public GamesResponse findGamesBySportIdAndTimePeriod(Long sportId, TimePeriod timePeriod) {
        if (timePeriod == null) {
            List<GameResponse> gameResponseList =
                    gameRepository.findAllByGameStatusAndSportId(GameStatus.ON_RECRUITING, sportId).stream()
                            .map(this::mapGameToGameResponse)
                            .toList();

            return new GamesResponse(gameResponseList);
        }

        List<GameResponse> gameResponseList = gameRepository
                .findAllByGameStatusAndSportIdAndTimePeriod(
                        Game.GameStatus.ON_RECRUITING, sportId, timePeriod.getStartTime(), timePeriod.getEndTime())
                .stream()
                .map(this::mapGameToGameResponse)
                .toList();

        return new GamesResponse(gameResponseList);
    }

    public GameDetailResponse findGameById(Long gameId) {
        Game game = gameRepository.findById(gameId).orElseThrow(() -> new EntityNotFoundException("해당 게임이 없습니다."));

        return mapGameToGameDetailResponse(game);
    }

    @Transactional
    public void cancelGamesByIds(List<Long> ids) {
        gameRepository.updateStatusToCanceled(ids);
    }

    public void sendCanceledAlarms(List<Long> ids) throws FirebaseMessagingException {
        List<GameParticipant> participants = gameParticipantRepository.findByGameIdIn(ids);

        List<Member> members =
            participants.stream().map(GameParticipant::getMember).toList();

        List<String> tokens = fcmTokenRepository.findAllByMemberIn(members).stream()
            .map(FcmToken::getToken)
            .toList();

        fcmService.sendMulticastPush(tokens, "매칭 취소", "인원이 모이지 않아 매칭이 취소되었습니다.");
    }

    public void sendMatchedAlarms(List<Long> ids) throws FirebaseMessagingException {
        List<GameParticipant> participants = gameParticipantRepository.findByGameIdIn(ids);

        notifyMatchingCompleted(participants);
    }

    private GameResponse mapGameToGameResponse(Game game) {
        int currentPlayerCount = gameParticipantRepository.findByGame(game).size();
        return new GameResponse(game, currentPlayerCount);
    }

    private GameDetailResponse mapGameToGameDetailResponse(Game game) {
        int currentPlayerCount = gameParticipantRepository.findByGame(game).size();
        return new GameDetailResponse(game, currentPlayerCount);
    }

    private void validateMemberIsVerified(Member member) {
        if (!member.getIsVerified()) {
            throw new IllegalStateException("학교 이메일 인증이 필요합니다.");
        }
    }

    private void addParticipant(Game game, Member member) {
        final GameParticipants gameParticipants = new GameParticipants(gameParticipantRepository.findByGame(game));

        gameParticipants.validateNotParticipated(member);
        gameParticipants.validateCapacity(game);

        gameParticipantRepository.save(new GameParticipant(member, game));

        if (gameParticipants.size() + 1 == game.getPlayerCount()) {
            // TODO - call alarm method
        }
    }

    private void notifyMatchingCompleted(List<GameParticipant> participants) throws FirebaseMessagingException {
        List<Member> members =
                participants.stream().map(GameParticipant::getMember).toList();

        List<String> tokens = fcmTokenRepository.findAllByMemberIn(members).stream()
                .map(FcmToken::getToken)
                .toList();

        fcmService.sendMulticastPush(tokens, "매칭 완료", "매칭이 완료되었습니다.");
    }

    public GamesResponse findGamesByMember(Member member) {
        List<GameResponse> gameResponseList = gameParticipantRepository.findAllByMember(member).stream()
                .map(GameParticipant::getGame)
            .map(this::mapGameToGameResponse)
            .toList();
        return new GamesResponse(gameResponseList);
    }

    public GamesResponse findGamesByMemberAndGameStatus(Member member, GameStatus gameStatus) {
        List<GameResponse> gameResponseList = gameParticipantRepository.findAllByMember(member).stream()
                .map(GameParticipant::getGame)
                .filter(game -> game.isStatus(gameStatus))
                .map(this::mapGameToGameResponse)
                .toList();
        return new GamesResponse(gameResponseList);
    }

    // TODO: 인원 모집이 완료됐던 매칭이 취소될 경우 알림 기능 구현 (참가자가 나갈 경우)
}
