package com.b4f2.pting.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.b4f2.pting.domain.FcmToken;
import com.b4f2.pting.domain.Member;

public interface FcmTokenRepository extends JpaRepository<FcmToken, Long> {

    Optional<FcmToken> findByMember(Member member);

    List<FcmToken> findAllByMemberIn(List<Member> members);
}
