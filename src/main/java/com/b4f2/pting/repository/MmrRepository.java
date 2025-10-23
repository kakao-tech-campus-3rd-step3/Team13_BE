package com.b4f2.pting.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.b4f2.pting.domain.Member;
import com.b4f2.pting.domain.Mmr;
import com.b4f2.pting.domain.Sport;

public interface MmrRepository extends JpaRepository<Mmr, Long> {

    Optional<Mmr> findByMemberAndSport(Member member, Sport sport);
}
