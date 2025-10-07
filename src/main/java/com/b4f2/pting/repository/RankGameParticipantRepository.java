package com.b4f2.pting.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.b4f2.pting.domain.Game;
import com.b4f2.pting.domain.RankGameParticipant;

public interface RankGameParticipantRepository extends JpaRepository<RankGameParticipant, Long> {

    List<RankGameParticipant> findAllByGame(Game game);
}
