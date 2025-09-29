package com.b4f2.pting.service;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.b4f2.pting.domain.Game;
import com.b4f2.pting.domain.GameParticipant;
import com.b4f2.pting.domain.GameParticipants;
import com.b4f2.pting.domain.Member;
import com.b4f2.pting.domain.Sport;
import com.b4f2.pting.domain.TimePeriod;
import com.b4f2.pting.dto.CreateGameRequest;
import com.b4f2.pting.dto.GameDetailResponse;
import com.b4f2.pting.dto.GameResponse;
import com.b4f2.pting.dto.GamesResponse;
import com.b4f2.pting.repository.GameParticipantRepository;
import com.b4f2.pting.repository.GameRepository;
import com.b4f2.pting.repository.SportRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GameService {

    private final GameRepository gameRepository;
    private final GameParticipantRepository gameParticipantRepository;
    private final SportRepository sportRepository;

    @Transactional
    public GameDetailResponse createGame(Member member, CreateGameRequest request) {
        validateMemberIsVerified(member);

        Sport sport = sportRepository.findById(request.sportId())
            .orElseThrow(() -> new EntityNotFoundException("해당 스포츠가 존재하지 않습니다."));

        Game game = Game.create(
            sport,
            request.name(),
            request.playerCount(),
            Game.GameStatus.ON_MATCHING,
            request.startTime(),
            request.duration(),
            request.description()
        );

        gameRepository.save(game);

        addParticipant(game, member);

        return new GameDetailResponse(game);
    }

    @Transactional
    public void joinGame(Member member, Long gameId) {
        validateMemberIsVerified(member);

        Game game = gameRepository.findById(gameId)
            .orElseThrow(() -> new EntityNotFoundException("해당 게임이 없습니다."));

        if (game.getGameStatus() != Game.GameStatus.ON_MATCHING) {
            throw new IllegalStateException("게임이 모집 중이 아닙니다.");
        }

        addParticipant(game, member);
    }

    @Transactional
    public int endMatchingGames(LocalDateTime deadLine) {
        return gameRepository.endMatchingGames(deadLine);
    }

    public GamesResponse findGamesBySportIdAndTimePeriod(Long sportId, TimePeriod timePeriod) {
        if (timePeriod == null) {
            List<GameResponse> gameResponseList = gameRepository
                .findAllByGameStatusAndSportId(Game.GameStatus.ON_MATCHING, sportId)
                .stream()
                .map(GameResponse::new)
                .toList();

            return new GamesResponse(gameResponseList);
        }

        List<GameResponse> gameResponseList = gameRepository
            .findAllByGameStatusAndSportIdAndTimePeriod(
                Game.GameStatus.ON_MATCHING,
                sportId,
                timePeriod.getStartTime(),
                timePeriod.getEndTime()
            )
            .stream()
            .map(GameResponse::new)
            .toList();

        return new GamesResponse(gameResponseList);
    }

    public GameDetailResponse findGameById(Long gameId) {
        Game game = gameRepository.findById(gameId)
            .orElseThrow(() -> new EntityNotFoundException("해당 게임이 없습니다."));

        return new GameDetailResponse(game);
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
    }
}
