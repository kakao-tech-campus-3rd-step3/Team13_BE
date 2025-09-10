package com.b4f2.pting.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.b4f2.pting.domain.Game;
import com.b4f2.pting.domain.GameParticipant;

public interface GameParticipantRepository extends JpaRepository<GameParticipant, Long> {

    List<GameParticipant> findByGame(Game game);
}
