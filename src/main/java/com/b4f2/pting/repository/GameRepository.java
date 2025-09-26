package com.b4f2.pting.repository;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.b4f2.pting.domain.Game;

public interface GameRepository extends JpaRepository<Game, Long> {

    List<Game> findAllByGameStatusAndSportId(Game.GameStatus status, Long sportId);

    @Query("""
            select g
            from Game g
            where g.sport.id = :sportId
                and g.gameStatus = :status
                and cast(g.startTime as localtime) between :startTime and :endTime
        """)
    List<Game> findAllByGameStatusAndSportIdAndTimePeriod(Game.GameStatus status, Long sportId, LocalTime startTime, LocalTime endTime);

    @Modifying
    @Query("""
            update Game g
            set g.gameStatus = 'END'
            where g.gameStatus = 'ON_MATCHING' and g.startTime <= :deadline
        """)
    int endMatchingGames(@Param("deadline") LocalDateTime deadLine);
}
