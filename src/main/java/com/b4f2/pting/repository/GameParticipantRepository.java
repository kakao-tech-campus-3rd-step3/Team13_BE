package com.b4f2.pting.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.b4f2.pting.domain.Game;
import com.b4f2.pting.domain.GameParticipant;

public interface GameParticipantRepository extends JpaRepository<GameParticipant, Long> {

    List<GameParticipant> findByGame(Game game);

    @Query("SELECT gp.member.id FROM GameParticipant gp WHERE gp.game.id = :gameId")
    List<Long> findMemberIdsByGameId(@Param("gameId") Long gameId);
}
