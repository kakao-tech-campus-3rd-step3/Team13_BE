package com.b4f2.pting.domain;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import com.pocketcombats.openskill.data.SimplePlayerResult;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "mmr")
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Mmr {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sport_id")
    private Sport sport;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(name = "mu")
    private Double mu;

    @Column(name = "sigma")
    private Double sigma;

    public Mmr(Sport sport, Member member) {
        this(null, sport, member, 25.0, 8.3);
    }


    public SimplePlayerResult<Long> getSimplePlayerResult() {
        return new SimplePlayerResult<>(id, mu, sigma);
    }

    public void setSimplePlayerResult(SimplePlayerResult<Long> simplePlayerResult) {
        if (!Objects.equals(id, simplePlayerResult.id())) {
            throw new IllegalArgumentException("Illegal Player Result");
        }

        mu = simplePlayerResult.mu();
        sigma = simplePlayerResult.sigma();
    }

    public boolean isId(Long id) {
        return Objects.equals(id, this.id);
    }

    public void updateMu(double mu) {
        this.mu = mu;
    }

    public void updateSigma(double sigma) {
        this.sigma = sigma;
    }
}
