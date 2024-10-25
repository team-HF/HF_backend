package com.hf.healthfriend.domain.matching.service;

import com.hf.healthfriend.domain.matching.dto.MatchingResponseDto;
import com.hf.healthfriend.domain.matching.entity.Matching;
import com.hf.healthfriend.domain.member.entity.Member;
import com.hf.healthfriend.domain.member.repository.MemberRepository;
import com.hf.healthfriend.testutil.SampleEntityGenerator;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@Slf4j
@ExtendWith(MockitoExtension.class)
class TestMatchingService {

    @InjectMocks
    MatchingService matchingService;

    @Mock
    MemberRepository memberRepository;

    @DisplayName("getAllMatchingOfMember - success")
    @Test
    void getMatchingOfMember_success() {
        // Given
        Member member1 = SampleEntityGenerator.generateSampleMember("asdf@gmail.com", "member1");
        member1.setId(1000L);
        Member member2 = SampleEntityGenerator.generateSampleMember("asdf1@gmail.com", "member2");
        member2.setId(1001L);
        Member member3 = SampleEntityGenerator.generateSampleMember("asdf2@gmail.com", "member3");
        member3.setId(1002L);

        new Matching(member3, member1, "스포애니", "서울시 영등포구 당산역", LocalDateTime.now().plusDays(2));
        new Matching(member1, member2, "에이블짐", "서울시 영등포구 당산역", LocalDateTime.now().plusDays(1));
        when(this.memberRepository.findById(member1.getId())).thenReturn(Optional.of(member1));

        // When
        List<MatchingResponseDto> matchingOfMember = this.matchingService.getAllMatchingOfMember(member1.getId());

        // Then
        assertThat(matchingOfMember).size().isEqualTo(2);
        assertThat(matchingOfMember.stream().map((e) -> e.getRequester().getMemberId()))
                .containsExactly(member3.getId(), member1.getId());
        assertThat(matchingOfMember.stream().map((e) -> e.getRequestTarget().getMemberId()))
                .containsExactly(member1.getId(), member2.getId());
    }
}