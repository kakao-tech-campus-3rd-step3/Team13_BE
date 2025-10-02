package com.b4f2.pting.service;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import com.b4f2.pting.domain.GameParticipants;
import com.b4f2.pting.domain.MatchResultVote;
import com.b4f2.pting.domain.Member;
import com.b4f2.pting.domain.RankGame;
import com.b4f2.pting.domain.RankGameTeam;
import com.b4f2.pting.dto.VoteRequest;
import com.b4f2.pting.dto.VoteResultResponse;
import com.b4f2.pting.repository.GameParticipantRepository;
import com.b4f2.pting.repository.MatchResultVoteRepository;
import com.b4f2.pting.repository.MemberRepository;
import com.b4f2.pting.repository.RankGameParticipantRepository;
import com.b4f2.pting.repository.RankGameRepository;

@Service
@RequiredArgsConstructor
public class RankGameService {

    private final RankGameParticipantRepository rankGameParticipantRepository;
    private final GameParticipantRepository gameParticipantRepository;
    private final RankGameRepository rankGameRepository;
    private final MemberRepository memberRepository;
    private final MatchResultVoteRepository matchResultVoteRepository;

    public VoteResultResponse voteMatchResult(Long gameId, VoteRequest voteRequest, Member member) {
        // Validation Response
        RankGame game = rankGameRepository.findById(gameId)
            .orElseThrow(() -> new IllegalArgumentException("게임을 찾을 수 없습니다."));

        if (!game.isEnded()) {
            throw new IllegalArgumentException("아직 게임이 끝나지 않았습니다.");
        }

        if (game.hasMemberVote(member)) {
            throw new IllegalArgumentException("이미 투표를 완료했습니다.");
        }

        // Update Vote
        MatchResultVote matchResultVote = new MatchResultVote(member, game, voteRequest.winTeam());
        matchResultVoteRepository.save(matchResultVote);

        game.vote(matchResultVote);

        checkAndHandle(game);

        // Result
        List<RankGameTeam> rankGameTeams = game.getMatchResultVoteList().stream().map(MatchResultVote::getVotedTeam).toList();
        return new VoteResultResponse(rankGameTeams);
    }

    private void checkAndHandle(RankGame game) {
        GameParticipants gameParticipants = new GameParticipants(gameParticipantRepository.findByGame(game));
        if  (gameParticipants.size() <= game.getNumOfVote()) {
            handleVoteResult(game);
        }
    }

    protected void handleVoteResult(RankGame game) {
        RankGameTeam winTeam = game.getWinTeam();
        // TODO - Update MMR
    }
}
