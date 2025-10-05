package com.b4f2.pting.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.b4f2.pting.domain.GameParticipants;
import com.b4f2.pting.domain.MatchResultVote;
import com.b4f2.pting.domain.Member;
import com.b4f2.pting.domain.Mmr;
import com.b4f2.pting.domain.MmrUpdater;
import com.b4f2.pting.domain.RankGame;
import com.b4f2.pting.domain.RankGameParticipant;
import com.b4f2.pting.domain.RankGameParticipants;
import com.b4f2.pting.domain.RankGameTeam;
import com.b4f2.pting.domain.Sport;
import com.b4f2.pting.dto.VoteRequest;
import com.b4f2.pting.dto.VoteResultResponse;
import com.b4f2.pting.repository.GameParticipantRepository;
import com.b4f2.pting.repository.MatchResultVoteRepository;
import com.b4f2.pting.repository.MemberRepository;
import com.b4f2.pting.repository.MmrRepository;
import com.b4f2.pting.repository.RankGameParticipantRepository;
import com.b4f2.pting.repository.RankGameRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RankGameService {

    private final RankGameParticipantRepository rankGameParticipantRepository;
    private final GameParticipantRepository gameParticipantRepository;
    private final RankGameRepository rankGameRepository;
    private final MemberRepository memberRepository;
    private final MatchResultVoteRepository matchResultVoteRepository;
    private final MmrRepository mmrRepository;

    @Transactional
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

        if (winTeam == RankGameTeam.NONE) {
            return;
        }

        RankGameParticipants rankGameParticipants = new RankGameParticipants(rankGameParticipantRepository.findAllByGame(game));

        List<Mmr> winMmrList = rankGameParticipants.getGameParticipantList()
            .stream()
            .filter(rankGameParticipant -> rankGameParticipant.isTeam(winTeam))
            .map(this::mapRankGameParticipantToMmr)
            .toList();

        List<Mmr> lossTeamMmrList = rankGameParticipants.getGameParticipantList()
            .stream()
            .filter(rankGameParticipant -> !(rankGameParticipant.isTeam(winTeam) || rankGameParticipant.isTeam(RankGameTeam.NONE)))
            .map(this::mapRankGameParticipantToMmr)
            .toList();

        MmrUpdater mmrUpdater = new MmrUpdater(winMmrList, lossTeamMmrList);
        mmrUpdater.update();
    }

    private Mmr mapRankGameParticipantToMmr(RankGameParticipant rankGameParticipant) {
        Member member = rankGameParticipant.getMember();
        Sport sport = rankGameParticipant.getGame().getSport();

        Optional<Mmr> mmrOptional = mmrRepository.findByMemberAndSport(
            rankGameParticipant.getMember(),
            rankGameParticipant.getGame().getSport()
        );

        return mmrOptional.orElseGet(() -> {
            Mmr mmr1 = new Mmr(sport, member);
            mmrRepository.save(mmr1);
            return mmr1;
        });
    }
}
