package com.b4f2.pting.repository.interest;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.b4f2.pting.domain.Member;
import com.b4f2.pting.domain.TimePeriod;
import com.b4f2.pting.domain.interest.UserInterestTime;

public interface UserInterestTimeRepository extends JpaRepository<UserInterestTime, Long> {

    boolean existsByMemberAndTimePeriod(Member member, TimePeriod timePeriod);

    void deleteByMemberAndTimePeriod(Member member, TimePeriod timePeriod);

    List<UserInterestTime> findByMember(Member member);
}
