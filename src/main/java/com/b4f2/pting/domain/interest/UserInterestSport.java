package com.b4f2.pting.domain.interest;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.b4f2.pting.domain.Member;
import com.b4f2.pting.domain.Sport;

@Entity
@Table(name = "user_interest_sport")
@AllArgsConstructor
@NoArgsConstructor
public class UserInterestSport {

    @Id
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Getter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sport_id")
    private Sport sport;

    public UserInterestSport(Member member, Sport sport) {
        this(null, member, sport);
    }
}
