package com.b4f2.pting.scheduler;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.google.firebase.messaging.FirebaseMessagingException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.b4f2.pting.repository.projection.ClosedGameSummary;
import com.b4f2.pting.service.GameService;

@Slf4j
@Component
@RequiredArgsConstructor
public class GameScheduler {

    private final GameService gameService;

    @Scheduled(cron = "0 */5 * * * *")
    public void endMatchingGamesJob() throws FirebaseMessagingException {
        LocalDateTime deadLine = LocalDateTime.now(ZoneId.of("Asia/Seoul")).plusHours(3);
        List<ClosedGameSummary> updated = gameService.endMatchingGames(deadLine);

        List<Long> cancel = new ArrayList<>();
        List<Long> close = new ArrayList<>();

        for (ClosedGameSummary summary : updated) {
            if (summary.currentPlayerCount() < summary.playerCount()) {
                cancel.add(summary.id());
            } else {
                close.add(summary.id());
            }
        }

        if (!cancel.isEmpty()) {
            gameService.cancelGamesByIds(cancel);
            gameService.sendCanceledAlarms(cancel);
        }

        if (!close.isEmpty()) {
            gameService.sendMatchedAlarms(close);
        }

        log.info("[GameScheduler] endMatchingGamesJob finished - updated rows: {}", updated.size());
    }
}
