package com.b4f2.pting.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.b4f2.pting.domain.Game;
import com.b4f2.pting.domain.GameParticipant;
import com.b4f2.pting.domain.Member;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface GameParticipantRepository extends JpaRepository<GameParticipant, Long> {

    List<GameParticipant> findByGame(Game game);

    List<GameParticipant> findAllByMember(Member member);

    void deleteByGameAndMember(Game game, Member member);

    List<GameParticipant> findByGameIdIn(List<Long> ids);
}
