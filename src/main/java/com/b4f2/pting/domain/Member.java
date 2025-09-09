package com.b4f2.pting.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "member", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"oauthId", "oauthProvider"})
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private Long oauthId;

    @NotNull
    @Enumerated(EnumType.STRING)
    private OAuthProvider oauthProvider;

    private String name;

    private String imageUrl;

    private String schoolEmail;

    private Boolean isVerified = false;

    public enum OAuthProvider {
        KAKAO
    }

    public Member(Long oauthId, OAuthProvider oauthProvider) {
        this.oauthId = oauthId;
        this.oauthProvider = oauthProvider;
    }

    public void updateSchoolEmail(String email) {
        this.schoolEmail = email;
    }

    public void markAsVerified() {
        this.isVerified = true;
    }
}
