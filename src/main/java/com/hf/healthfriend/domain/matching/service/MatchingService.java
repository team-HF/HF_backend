package com.hf.healthfriend.domain.matching.service;

import com.hf.healthfriend.domain.matching.dto.request.MatchingRequestDto;
import com.hf.healthfriend.domain.matching.entity.Matching;
import com.hf.healthfriend.domain.matching.exception.InvalidLevelMatchingException;
import com.hf.healthfriend.domain.matching.repository.MatchingRepository;
import com.hf.healthfriend.domain.member.constant.FitnessLevel;
import com.hf.healthfriend.domain.member.entity.Member;
import com.hf.healthfriend.domain.member.exception.MemberNotFoundException;
import com.hf.healthfriend.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MatchingService {
    private final MatchingRepository matchingRepository;
    private final MemberRepository memberRepository;

    public Long requestMatching(MatchingRequestDto requestDto) {
        Member requester = this.memberRepository.findById(requestDto.getRequesterId())
                .orElseThrow(() -> new MemberNotFoundException("요청하는 회원의 ID가 존재하지 않습니다: " + requestDto.getRequesterId()));
        Member target = this.memberRepository.findById(requestDto.getTargetId())
                .orElseThrow(() -> new MemberNotFoundException("요청 대상의 ID가 존재하지 않습니다: " + requestDto.getTargetId()));

        FitnessLevel requesterLevel = requester.getFitnessLevel();
        if (target.getFitnessLevel() == requesterLevel) {
            throw new InvalidLevelMatchingException(requesterLevel, target.getFitnessLevel());
        }

        Matching newMatching = requesterLevel == FitnessLevel.BEGINNER
                ? new Matching(requester, target, requestDto.getMeetingTime())
                : new Matching(target, requester, requestDto.getMeetingTime());

        Matching savedMatching = this.matchingRepository.save(newMatching);
        return savedMatching.getMatchingId();
    }
}
