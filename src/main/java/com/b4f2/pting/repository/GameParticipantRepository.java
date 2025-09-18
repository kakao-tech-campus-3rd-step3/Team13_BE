package com.b4f2.pting.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.b4f2.pting.domain.Game;
import com.b4f2.pting.domain.GameParticipant;
import com.b4f2.pting.domain.Member;

public interface GameParticipantRepository extends JpaRepository<GameParticipant, Long> {

    List<GameParticipant> findByGame(Game game);

    @Query("SELECT gp.member FROM GameParticipant gp WHERE gp.game.id = :gameId")
    List<Member> findMembersByGameId(@Param("gameId") Long gameId);
}
