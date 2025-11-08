package com.b4f2.pting.repository.interest;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.b4f2.pting.domain.Member;
import com.b4f2.pting.domain.Sport;
import com.b4f2.pting.domain.interest.UserInterestSport;

public interface UserInterestSportRepository extends JpaRepository<UserInterestSport, Long> {

    boolean existsByMemberAndSport(Member member, Sport sport);

    void deleteByMemberAndSport(Member member, Sport sport);

    List<UserInterestSport> findByMember(Member member);
}
