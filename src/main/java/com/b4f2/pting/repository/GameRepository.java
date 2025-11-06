package com.b4f2.pting.repository;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.b4f2.pting.domain.Game;
import com.b4f2.pting.repository.projection.ClosedGameSummary;

public interface GameRepository extends JpaRepository<Game, Long> {

    List<Game> findAllByGameStatusAndSportId(Game.GameStatus status, Long sportId);

    @Query("""
            select g
            from Game g
            where g.sport.id = :sportId
                and g.gameStatus = :status
                and cast(g.startTime as localtime) between :startTime and :endTime
        """)
    List<Game> findAllByGameStatusAndSportIdAndTimePeriod(
            Game.GameStatus status, Long sportId, LocalTime startTime, LocalTime endTime);

    @Modifying
    @Query(value = """
            with updated as (
                            update game
                                set game_status = 'CLOSED'
                            where start_time <= :deadline
                                and (game_status = 'ON_RECRUITING'
                                    or game_status = 'FULL')
                            returning *, 'Game' as clazz_
            )
            select u.id, u.player_count,
                   coalesce(count(gu.id), 0)::int as current_player_count
            from updated u
            left join game_user gu on u.id = gu.game_id
            group by u.id, u.player_count
            """, nativeQuery = true)
    List<ClosedGameSummary> endMatchingGames(@Param("deadline") LocalDateTime deadLine);

    @Modifying
    @Query(value = """
            update game
                set game_status = 'CANCELED'
            where id in :ids
            """, nativeQuery = true)
    void updateStatusToCanceled(@Param("ids") List<Long> ids);
}
