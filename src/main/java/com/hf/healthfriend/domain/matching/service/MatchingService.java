package com.hf.healthfriend.domain.matching.service;

import com.hf.healthfriend.domain.matching.constant.MatchingStatus;
import com.hf.healthfriend.domain.matching.dto.MatchingDto;
import com.hf.healthfriend.domain.matching.dto.request.MatchingRequestDto;
import com.hf.healthfriend.domain.matching.entity.Matching;
import com.hf.healthfriend.domain.matching.exception.InvalidLevelMatchingException;
import com.hf.healthfriend.domain.matching.repository.MatchingRepository;
import com.hf.healthfriend.domain.member.constant.FitnessLevel;
import com.hf.healthfriend.domain.member.dto.MemberDto;
import com.hf.healthfriend.domain.member.entity.Member;
import com.hf.healthfriend.domain.member.exception.MemberNotFoundException;
import com.hf.healthfriend.domain.member.repository.MemberRepository;
import com.hf.healthfriend.global.util.mapping.BeanMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
@RequiredArgsConstructor
public class MatchingService {
    private final MatchingRepository matchingRepository;
    private final MemberRepository memberRepository;
    private final BeanMapper beanMapper;

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

    public void updateMatchingStatus(Long matchingId, MatchingStatus matchingStatus) {
        Matching matching = this.matchingRepository.findById(matchingId)
                .orElseThrow(NoSuchElementException::new);
        switch (matchingStatus) {
            case ACCEPTED -> matching.accept();
            case REJECTED -> matching.reject();
            case FINISHED -> matching.finish();
        }
    }

    public List<MatchingDto> getMatchingOfMember(Long memberId) {
        Member member = this.memberRepository.findById(memberId)
                .orElseThrow(NoSuchElementException::new);
        return (member.getFitnessLevel() == FitnessLevel.ADVANCED
                ? member.getMatchingsAsAdvanced()
                : member.getMatchingsAsBeginner())
                .stream()
                .map((m) -> {
                    MatchingDto matchingDto = this.beanMapper.generateBean(m, MatchingDto.class);
                    matchingDto.setTargetMember(
                            this.beanMapper.generateBean(
                                    // 조회하려는 회원이 고수면 상대는 새싹일 테므로
                                    member.getFitnessLevel() == FitnessLevel.ADVANCED
                                            ? m.getBeginner()
                                            : m.getAdvanced(),
                                    MemberDto.class
                            )
                    );
                    return matchingDto;
                })
                .toList();
    }
}
