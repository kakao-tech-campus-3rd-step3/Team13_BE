package com.b4f2.pting.domain.interest;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.b4f2.pting.domain.Member;
import com.b4f2.pting.domain.TimePeriod;

@Table(name = "user_interest_time")
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class UserInterestTime {

    @Id
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Getter
    @Enumerated(EnumType.STRING)
    @Column(name = "time_period")
    private TimePeriod timePeriod;

    public UserInterestTime(Member member, TimePeriod timePeriod) {
        this(null, member, timePeriod);
    }
}
