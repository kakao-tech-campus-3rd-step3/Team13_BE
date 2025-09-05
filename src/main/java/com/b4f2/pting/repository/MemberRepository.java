package com.b4f2.pting.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.b4f2.pting.domain.Member;
import com.b4f2.pting.domain.Member.OAuthProvider;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByOauthIdAndOauthProvider(Long oauthId, OAuthProvider provider);
}
