package com.b4f2.pting.domain;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import lombok.Getter;

@Entity
@Table(name = "rank_game")
@Getter
public class RankGame extends Game {

    private static final double VOTE_APPROVAL_THRESHOLD = 0.6;

    @OneToMany(mappedBy = "game")
    private List<MatchResultVote> matchResultVoteList = new ArrayList<>();

    public RankGame() {
        super();
    }

    public RankGame(
            Long id,
            Sport sport,
            String name,
            String gameLocation,
            Integer playerCount,
            GameStatus gameStatus,
            LocalDateTime startTime,
            Integer duration,
            String description,
            String imageUrl) {
        super(id, sport, name, gameLocation, playerCount, gameStatus, startTime, duration, description, imageUrl);
    }

    public static RankGame create(
            Sport sport,
            String name,
            String gameLocation,
            Integer playerCount,
            GameStatus gameStatus,
            LocalDateTime startTime,
            Integer duration,
            String description,
            String imageUrl) {
        LocalDateTime nowInSeoul = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
        if (startTime.isBefore(nowInSeoul)) {
            throw new IllegalArgumentException("매치 시작 시간은 현재 시간보다 이후여야 합니다.");
        }

        return new RankGame(
                null, sport, name, gameLocation, playerCount, gameStatus, startTime, duration, description, imageUrl);
    }

    public boolean hasMemberVote(Member member) {
        return matchResultVoteList.stream().anyMatch(matchResultVote -> matchResultVote.isMemberVote(member));
    }

    public void vote(MatchResultVote matchResultVote) {
        matchResultVoteList.add(matchResultVote);
    }

    public int getNumOfVote() {
        return matchResultVoteList.size();
    }

    public RankGameTeam getWinTeam() {
        long numOfBlueVote = matchResultVoteList.stream()
                .filter(matchResultVote -> matchResultVote.isWinTeam(RankGameTeam.BLUE_TEAM))
                .count();
        long numOfRedVote = matchResultVoteList.stream()
                .filter(matchResultVote -> matchResultVote.isWinTeam(RankGameTeam.RED_TEAM))
                .count();
        long numOfVote = getNumOfVote();

        if (numOfVote == 0) {
            return RankGameTeam.NONE;
        }

        double redRatio = (double) numOfRedVote / (double) numOfVote;
        double blueRatio = (double) numOfBlueVote / (double) numOfVote;

        if (redRatio >= VOTE_APPROVAL_THRESHOLD) {
            return RankGameTeam.RED_TEAM;
        } else if (blueRatio >= VOTE_APPROVAL_THRESHOLD) {
            return RankGameTeam.BLUE_TEAM;
        } else {
            return RankGameTeam.NONE;
        }
    }
}
