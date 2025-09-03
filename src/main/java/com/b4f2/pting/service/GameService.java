package com.b4f2.pting.service;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.b4f2.pting.domain.Game;
import com.b4f2.pting.domain.GameUser;
import com.b4f2.pting.domain.Member;
import com.b4f2.pting.domain.Sport;
import com.b4f2.pting.dto.CreateGameRequest;
import com.b4f2.pting.dto.GameResponse;
import com.b4f2.pting.repository.SportRepository;
import com.b4f2.pting.repository.GameRepository;
import com.b4f2.pting.repository.GameUserRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GameService {

    private final GameRepository gameRepository;
    private final GameUserRepository gameUserRepository;
    private final SportRepository sportRepository;

    @Transactional
    public GameResponse createGame(Member member, CreateGameRequest request) {
        if (!member.getIsVerified()) {
            throw new IllegalStateException("학교 이메일 인증이 필요합니다.");
        }

        Sport sport = sportRepository.findById(request.sportId())
            .orElseThrow(() -> new EntityNotFoundException("해당 스포츠가 존재하지 않습니다."));

        ZonedDateTime nowInSeoul = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
        ZonedDateTime requestedStartTimeInSeoul = request.startTime().atZone(ZoneId.of("Asia/Seoul"));
        if (requestedStartTimeInSeoul.isBefore(nowInSeoul)) {
            throw new IllegalArgumentException("매치 시작 시간은 현재 시간보다 이후여야 합니다.");
        }

        Game game = new Game(
            sport,
            request.playerCount(),
            Game.GameStatus.ON_MATCHING,
            request.startTime(),
            request.duration()
        );

        gameRepository.save(game);

        addParticipant(game, member);

        return new GameResponse(game);
    }

    private void addParticipant(Game game, Member member) {
        if (gameUserRepository.existsByMemberIdAndGame(member.getId(), game)) {
            throw new IllegalStateException("이미 참여한 게임입니다.");
        }

        int currentPlayerCount = gameUserRepository.countByGame(game);
        if (currentPlayerCount >= game.getPlayerCount()) {
            throw new IllegalStateException("모집 인원이 마감되었습니다.");
        }

        gameUserRepository.save(new GameUser(member.getId(), game));
    }
}
