package com.b4f2.pting.scheduler;

import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.b4f2.pting.domain.Sport;
import com.b4f2.pting.repository.SportRepository;
import com.b4f2.pting.service.MatchingService;

@Slf4j
@Component
@RequiredArgsConstructor
public class RankGameScheduler {

    private final MatchingService matchingService;
    private final SportRepository sportRepository;

    @Scheduled(cron = "0 0 9 * * ?")
    public void dailyGameCreationJob() {
        List<Sport> allSports = sportRepository.findAll();

        for (Sport sport : allSports) {
            matchingService.proposeGamesFromQueue(sport);
        }

        log.info("[RankGameScheduler] dailyGameCreationJob finished");
    }
}
