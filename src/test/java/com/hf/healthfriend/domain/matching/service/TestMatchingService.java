package com.hf.healthfriend.domain.matching.service;

import com.hf.healthfriend.domain.matching.dto.MatchingDto;
import com.hf.healthfriend.domain.matching.entity.Matching;
import com.hf.healthfriend.domain.matching.repository.MatchingRepository;
import com.hf.healthfriend.domain.member.constant.FitnessLevel;
import com.hf.healthfriend.domain.member.entity.Member;
import com.hf.healthfriend.domain.member.repository.MemberRepository;
import com.hf.healthfriend.testutil.SampleEntityGenerator;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest
@Transactional
class TestMatchingService {

    @Autowired
    MatchingService matchingService;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    MatchingRepository matchingRepository;

    @DisplayName("getMatchingOfMember - success: case of advanced")
    @Test
    void getMatchingOfMember_success_caseOfAdvanced() {
        // Given
        Member advanced = SampleEntityGenerator.generateSampleMember("asdf@gmail.com", "advanced");
        advanced.setFitnessLevel(FitnessLevel.ADVANCED);
        Member beginner1 = SampleEntityGenerator.generateSampleMember("asdf1@gmail.com", "beginner1");
        beginner1.setFitnessLevel(FitnessLevel.BEGINNER);
        Member beginner2 = SampleEntityGenerator.generateSampleMember("asdf2@gmail.com", "beginner2");
        beginner2.setFitnessLevel(FitnessLevel.BEGINNER);
        this.memberRepository.save(advanced);
        this.memberRepository.save(beginner1);
        this.memberRepository.save(beginner2);

        Matching matching1 = new Matching(beginner1, advanced, LocalDateTime.now().plusDays(1));
        Matching matching2 = new Matching(beginner2, advanced, LocalDateTime.now().plusDays(1));
        this.matchingRepository.save(matching1);
        this.matchingRepository.save(matching2);

        // When
        List<MatchingDto> matchingOfMember = this.matchingService.getMatchingOfMember(advanced.getId());

        // Then
        assertThat(matchingOfMember).size().isEqualTo(2);
        assertThat(matchingOfMember.stream().map((dto) -> dto.getTargetMember().getMemberId()))
                .containsExactlyInAnyOrder(beginner1.getId(), beginner2.getId());
    }

    @DisplayName("getMatchingOfMember - success: case of beginner")
    @Test
    void getMatchingOfMember_success_caseOfBeginner() {
        // Given
        Member beginner = SampleEntityGenerator.generateSampleMember("asdf@gmail.com", "beginner");
        beginner.setFitnessLevel(FitnessLevel.BEGINNER);
        Member advanced1 = SampleEntityGenerator.generateSampleMember("asdf1@gmail.com", "advanced1");
        advanced1.setFitnessLevel(FitnessLevel.ADVANCED);
        Member advanced2 = SampleEntityGenerator.generateSampleMember("asdf2@gmail.com", "advanced2");
        advanced2.setFitnessLevel(FitnessLevel.ADVANCED);
        beginner = this.memberRepository.save(beginner);
        advanced1 = this.memberRepository.save(advanced1);
        advanced2 = this.memberRepository.save(advanced2);

        Matching matching1 = new Matching(beginner, advanced1, LocalDateTime.now().plusDays(1));
        Matching matching2 = new Matching(beginner, advanced2, LocalDateTime.now().plusDays(1));
        this.matchingRepository.save(matching1);
        this.matchingRepository.save(matching2);

        // When
        List<MatchingDto> matchingOfMember = this.matchingService.getMatchingOfMember(beginner.getId());

        // Then
        assertThat(matchingOfMember).size().isEqualTo(2);
        assertThat(matchingOfMember.stream().map((dto) -> dto.getTargetMember().getMemberId()))
                .containsExactlyInAnyOrder(advanced1.getId(), advanced2.getId());
    }
}