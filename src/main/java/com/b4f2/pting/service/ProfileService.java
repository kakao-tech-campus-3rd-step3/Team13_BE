package com.b4f2.pting.service;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.b4f2.pting.domain.Member;
import com.b4f2.pting.dto.ProfileResponse;
import com.b4f2.pting.repository.MemberRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProfileService {

    private final MemberRepository memberRepository;

    public ProfileResponse getProfile(Long memberId) {
        Member member = getMemberOrThrowException(memberId);
        return new ProfileResponse(member);
    }

    @Transactional
    public ProfileResponse updateName(Long memberId, String name) {
        Member member = getMemberOrThrowException(memberId);

        member.changeName(name);

        return new ProfileResponse(member);
    }

    private Member getMemberOrThrowException(Long memberId) {
        return memberRepository.findById(memberId).orElseThrow(() -> new EntityNotFoundException("해당 유저는 존재하지 않습니다."));
    }

    @Transactional
    public ProfileResponse updateDescription(Long memberId, String description) {
        Member member = getMemberOrThrowException(memberId);

        member.changeDescription(description);

        return new ProfileResponse(member);
    }

    @Transactional
    public ProfileResponse updateImageUrl(Long memberId, String imageUrl) {
        Member member = getMemberOrThrowException(memberId);

        member.changeImageUrl(imageUrl);

        return new ProfileResponse(member);
    }
}
