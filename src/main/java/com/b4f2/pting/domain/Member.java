package com.b4f2.pting.domain;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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
    @Column(name = "oauth_id")
    private Long oauthId;

    @NotNull
    @Enumerated(EnumType.STRING)

    @Column(name = "oauth_provider")
    private OAuthProvider oauthProvider;

    @Column(name = "name")
    private String name;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "description")
    private String description;

    @Column(name = "school_email")
    private String schoolEmail;

    @Column(name = "is_verified")
    private Boolean isVerified = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_id")
    private School school;

    @OneToMany(mappedBy = "member")
    private List<Mmr> mmrList = new ArrayList<>();

    public enum OAuthProvider {
        KAKAO
    }

    public Member(Long oauthId, OAuthProvider oauthProvider) {
        this.oauthId = oauthId;
        this.oauthProvider = oauthProvider;
    }

    public Optional<School> getSchool() {
        return Optional.ofNullable(school);
    }

    public void changeName(String name) {
        this.name = name;
    }

    public void changeImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void changeDescription(String description) {
        this.description = description;
    }

    public void updateSchoolEmail(String email) {
        this.schoolEmail = email;
    }

    public void markAsVerified() {
        this.isVerified = true;
    }

    public void updateSchool(School school) {
        this.school = school;
    }

    public boolean isEqualMember(Member member) {
        return id.equals(member.id);
    }

    public boolean isMySchoolEmail(String email) {
        return schoolEmail.equals(email);
    }

    public boolean isVerifiedEmail(String email) {
        return isVerified && isMySchoolEmail(email);
    }
}
