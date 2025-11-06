package com.b4f2.pting.service;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.b4f2.pting.domain.Member;
import com.b4f2.pting.domain.MemberStatus;
import com.b4f2.pting.repository.MemberRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminMemberService {

    private final MemberRepository memberRepository;

    // 정지 해제
    @Transactional
    public void unSuspendMember(Long memberId) {
        Member member =
                memberRepository.findById(memberId).orElseThrow(() -> new EntityNotFoundException("회원을 찾을 수 없습니다."));
        member.changeStatus(MemberStatus.ACTIVE);
    }

    // 영구 제재
    @Transactional
    public void banMember(Long memberId) {
        Member member =
                memberRepository.findById(memberId).orElseThrow(() -> new EntityNotFoundException("회원을 찾을 수 없습니다."));
        member.changeStatus(MemberStatus.BANNED);
    }
}
