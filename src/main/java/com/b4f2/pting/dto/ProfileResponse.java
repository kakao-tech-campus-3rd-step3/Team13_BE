package com.b4f2.pting.dto;

import com.b4f2.pting.domain.Member;

public record ProfileResponse(String name, String email, String imageUrl, String description) {

    public ProfileResponse(Member member) {
        this(member.getName(), member.getSchoolEmail(), member.getImageUrl(), member.getDescription());
    }
}
