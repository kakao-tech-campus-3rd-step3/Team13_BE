package com.b4f2.pting.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "member")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @NotNull
    String oauthId;

    @NotNull
    @Enumerated(EnumType.STRING)
    OauthProvider oauthProvider;

    @NotNull
    String nickname;

    String schoolEmail;

    Boolean isVerified = false;

    public enum OauthProvider {
        KAKAO
    }
}
