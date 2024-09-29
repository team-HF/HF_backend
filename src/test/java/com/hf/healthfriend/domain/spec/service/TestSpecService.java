package com.hf.healthfriend.domain.spec.service;

import com.hf.healthfriend.domain.member.entity.Member;
import com.hf.healthfriend.domain.member.repository.MemberRepository;
import com.hf.healthfriend.domain.spec.dto.SpecDto;
import com.hf.healthfriend.testutil.SampleEntityGenerator;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest
@Transactional
class TestSpecService {

    @Autowired
    SpecService specService;

    @Autowired
    MemberRepository memberRepository;

    Member member;

    @BeforeEach
    void beforeEach() {
        Member member = SampleEntityGenerator.generateSampleMember("rudeh1253@gmail.com");
        this.memberRepository.save(member);
        this.member = member;
    }

    static Stream<Arguments> addSpec_success() {
        return Stream.of(
                Arguments.of(
                        LocalDate.of(1997, 9, 16),
                        LocalDate.of(2024, 2, 5),
                        false,
                        "sample-title",
                        "sample-description"
                ),
                Arguments.of(
                        // endDate는 Nullable
                        LocalDate.of(1997, 9, 16),
                        null,
                        false,
                        "sample-title",
                        "sample-description"
                )
        );
    }

    @DisplayName("addSpec - 성공적으로 생성")
    @MethodSource
    @ParameterizedTest
    void addSpec_success(LocalDate startDate, LocalDate endDate, boolean isCurrent, String title, String description) {
        SpecDto specDto = new SpecDto(
                startDate,
                endDate,
                isCurrent,
                title,
                description
        );

        Long generatedId = this.specService.addSpec(this.member.getId(), specDto);
        assertThat(generatedId).isNotNull();
    }
}