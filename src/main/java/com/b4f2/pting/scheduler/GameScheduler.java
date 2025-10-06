package com.b4f2.pting.scheduler;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.b4f2.pting.domain.Game;
import com.b4f2.pting.service.GameService;

@Slf4j
@Component
@RequiredArgsConstructor
public class GameScheduler {

    private final GameService gameService;

    @Scheduled(cron = "0 */5 * * * *")
    public void endMatchingGamesJob() {
        LocalDateTime deadLine = LocalDateTime.now(ZoneId.of("Asia/Seoul")).minusHours(3);
        List<Game> updated = gameService.endMatchingGames(deadLine);

        // TODO - call alarm method, check game is full(if game is not full -> delete game)
        /*
        updated.stream()
               .forEach(this::sendAlarm);
        */

        log.info("[GameScheduler] endMatchingGamesJob finished - updated rows: {}", updated.size());
    }
}
