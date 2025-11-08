package com.b4f2.pting.aspect;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import com.b4f2.pting.domain.Member;
import com.b4f2.pting.domain.MemberStatus;
import com.b4f2.pting.repository.MemberRepository;

@Aspect
@Component
@RequiredArgsConstructor
public class MemberStatusAspect {

    private final MemberRepository memberRepository;

    @Before("@annotation(CheckMemberStatus) && args(memberId,..)")
    public void checkStatus(Long memberId) {
        Member member =
                memberRepository.findById(memberId).orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));

        if (member.getStatus() == MemberStatus.SUSPENDED) {
            throw new IllegalStateException("정지된 계정은 이 기능을 사용할 수 없습니다.");
        } else if (member.getStatus() == MemberStatus.BANNED) {
            throw new IllegalStateException("영구 제재된 계정은 접근할 수 없습니다.");
        }
    }
}
