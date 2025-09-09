package com.b4f2.pting.service;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import com.b4f2.pting.domain.Member;
import com.b4f2.pting.repository.MemberRepository;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    public Member getMemberById(Long memberId) {
        return memberRepository.findById(memberId)
            .orElseThrow(() -> new EntityNotFoundException("회원 저옵가 존재하지 않습니다."));
    }
}
