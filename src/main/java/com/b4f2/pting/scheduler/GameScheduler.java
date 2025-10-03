package com.b4f2.pting.scheduler;

import java.time.LocalDateTime;
import java.time.ZoneId;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.b4f2.pting.service.GameService;

@Slf4j
@Component
@RequiredArgsConstructor
public class GameScheduler {

    private final GameService gameService;

    @Scheduled(cron = "0 */5 * * * *")
    public void endMatchingGamesJob() {
        LocalDateTime deadLine = LocalDateTime.now(ZoneId.of("Asia/Seoul")).minusHours(3);
        int updated = gameService.endMatchingGames(deadLine);
        log.info("[GameScheduler] endMatchingGamesJob finished - updated rows: {}", updated);
    }
}
