package com.b4f2.pting.repository;

import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.b4f2.pting.domain.Game;

public interface GameRepository extends JpaRepository<Game, Long> {
    @Query("select g from Game g where g.sport.id = :sportId and g.gameStatus = 'ON_MATCHING'")
    List<Game> findOnMatchingGamesBySportId(Long sportId);

    @Query("""
        select g
        from Game g
        where g.sport.id = :sportId
            and g.gameStatus = 'ON_MATCHING'
            and cast(g.startTime as localtime) between :startTime and :endTime
    """)
    List<Game> findOnMatchingGamesBySportIdAndTimePeriod(Long sportId, LocalTime startTime, LocalTime endTime);
}
