package com.b4f2.pting.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.b4f2.pting.domain.Member;
import com.b4f2.pting.domain.Sport;
import com.b4f2.pting.domain.TimePeriod;
import com.b4f2.pting.domain.interest.UserInterestSport;
import com.b4f2.pting.domain.interest.UserInterestTime;
import com.b4f2.pting.dto.SportResponse;
import com.b4f2.pting.dto.SportsResponse;
import com.b4f2.pting.dto.TimePeriodsResponse;
import com.b4f2.pting.repository.SportRepository;
import com.b4f2.pting.repository.interest.UserInterestSportRepository;
import com.b4f2.pting.repository.interest.UserInterestTimeRepository;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SubscribeService {

    private final UserInterestSportRepository userInterestSportRepository;
    private final UserInterestTimeRepository userInterestTimeRepository;
    private final SportRepository sportRepository;

    public void addInterestTime(Member member, TimePeriod timePeriod) {
        if (userInterestTimeRepository.existsByMemberAndTimePeriod(member, timePeriod)) {
            return;
        }

        userInterestTimeRepository.save(new UserInterestTime(member, timePeriod));
    }

    public void addInterestSport(Member member, Long sportId) {
        Sport sport = sportRepository.findById(sportId).orElse(null);

        if (sport == null) {
            throw new IllegalArgumentException("해당 스포츠를 찾을 수 없습니다.");
        }

        if (userInterestSportRepository.existsByMemberAndSport(member, sport)) {
            return;
        }

        userInterestSportRepository.save(new UserInterestSport(member, sport));
    }

    public void deleteInterestTime(Member member, TimePeriod timePeriod) {
        userInterestTimeRepository.deleteByMemberAndTimePeriod(member, timePeriod);
    }

    public void deleteInterestSport(Member member, Long sportId) {
        Sport sport = sportRepository.findById(sportId).orElse(null);

        if (sport == null) {
            throw new IllegalArgumentException("해당 스포츠를 찾을 수 없습니다.");
        }

        userInterestSportRepository.deleteByMemberAndSport(member, sport);
    }

    public TimePeriodsResponse getTimesByMember(Member member) {
        List<TimePeriod> timePeriodList = userInterestTimeRepository.findByMember(member)
            .stream()
            .map(UserInterestTime::getTimePeriod)
            .toList();

        return new TimePeriodsResponse(timePeriodList);
    }

    public SportsResponse getSportsByMember(Member member) {
        List<SportResponse> sportResponseList = userInterestSportRepository.findByMember(member)
            .stream()
            .map(UserInterestSport::getSport)
            .map(SportResponse::new)
            .toList();

        return new SportsResponse(sportResponseList);
    }
}
