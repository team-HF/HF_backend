package com.hf.healthfriend.domain.matching.service;

import com.hf.healthfriend.domain.matching.constant.MatchingFetchType;
import com.hf.healthfriend.domain.matching.constant.MatchingStatus;
import com.hf.healthfriend.domain.matching.dto.MatchingResponseDto;
import com.hf.healthfriend.domain.matching.dto.request.MatchingRequestDto;
import com.hf.healthfriend.domain.matching.entity.Matching;
import com.hf.healthfriend.domain.matching.repository.MatchingRepository;
import com.hf.healthfriend.domain.member.entity.Member;
import com.hf.healthfriend.domain.member.exception.MemberNotFoundException;
import com.hf.healthfriend.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Stream;

@Service
@Transactional
@RequiredArgsConstructor
public class MatchingService {
    private static final Comparator<Matching> MATCHING_LIST_SORT_COMPARATOR = (d1, d2) -> {
        LocalDateTime creationTime1 = d1.getMeetingTime();
        LocalDateTime creationTime2 = d2.getMeetingTime();
        if (creationTime1.isAfter(creationTime2)) {
            return -1;
        } else if (creationTime1.equals(creationTime2)) {
            return 0;
        } else {
            return 1;
        }
    };

    private final MatchingRepository matchingRepository;
    private final MemberRepository memberRepository;

    public Long requestMatching(MatchingRequestDto requestDto) {
        try {
            Matching savedMatching = this.matchingRepository.save(
                    new Matching(
                            new Member(requestDto.getRequesterId()),
                            new Member(requestDto.getTargetId()),
                            requestDto.getMeetingTime()
                    )
            );
            return savedMatching.getMatchingId();
        } catch (DataIntegrityViolationException e) {
            throw new MemberNotFoundException(e);
        }
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

    @Deprecated
    public List<MatchingResponseDto> getAllMatchingOfMember(Long memberId) {
        Member member = this.memberRepository.findById(memberId)
                .orElseThrow(NoSuchElementException::new);
        // TODO: Member에 있는 Matching 연관관계 필드명 수정해야 함
        // 충돌 방지를 위해 PR 머지 후 수정할 예정
        return Stream.concat(member.getMatchingRequests().stream(), member.getMatchingsReceived().stream())
                .sorted(MATCHING_LIST_SORT_COMPARATOR)
                .map(MatchingResponseDto::of)
                .toList();
    }

    public List<MatchingResponseDto> getMatchingOfMember(Long memberId, MatchingFetchType fetchType) {
        Member member = this.memberRepository.findById(memberId)
                .orElseThrow(NoSuchElementException::new);
        return switch (fetchType) {
            case ALL -> convertStreamToMatchingResponseDtoList(
                    Stream.concat(member.getMatchingRequests().stream(), member.getMatchingsReceived().stream())
            );
            case WHAT_I_REQUESTED -> convertStreamToMatchingResponseDtoList(member.getMatchingRequests().stream());
            case WHAT_I_RECEIVED -> convertStreamToMatchingResponseDtoList(member.getMatchingsReceived().stream());
        };
    }

    private List<MatchingResponseDto> convertStreamToMatchingResponseDtoList(Stream<Matching> matchingStream) {
        return matchingStream
                .sorted(MATCHING_LIST_SORT_COMPARATOR)
                .map(MatchingResponseDto::of)
                .toList();
    }
}
