package com.b4f2.pting.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.b4f2.pting.domain.Game;
import com.b4f2.pting.domain.GameUser;

public interface GameUserRepository extends JpaRepository<GameUser, Long> {

    boolean existsByMemberIdAndGame(Long memberId, Game game);

    Integer countByGame(Game game);
}
