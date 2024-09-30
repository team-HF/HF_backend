package com.hf.healthfriend.domain.spec.service;

import com.hf.healthfriend.domain.member.entity.Member;
import com.hf.healthfriend.domain.member.repository.MemberRepository;
import com.hf.healthfriend.domain.spec.dto.SpecDto;
import com.hf.healthfriend.testutil.SampleEntityGenerator;
import jakarta.validation.ValidationException;
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
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

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
                ),
                Arguments.of(
                        // startDate와 endDate는 같을 수 있음
                        LocalDate.of(2017, 3, 2),
                        LocalDate.of(2017, 3, 2),
                        false,
                        "sample-title",
                        "sample-description"
                ),
                Arguments.of(
                        // description은 빈 문자열이어도 됨
                        LocalDate.of(2017, 3, 2),
                        LocalDate.of(2017, 3, 2),
                        false,
                        "sample-title",
                        ""
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

        List<Long> generatedIds = this.specService.addSpec(this.member.getId(), List.of(specDto));
        generatedIds.forEach((id) -> assertThat(id).isNotNull());
    }

    static Stream<Arguments> addSpec_validationFail() {
        return Stream.of(
                Arguments.arguments(
                        "startDate는 NotNull",
                        null,
                        LocalDate.of(2018, 2, 7),
                        false,
                        "sample-title",
                        "sample-description"
                ),
                Arguments.of(
                        "시작 시간이 현재보다 나중이면 안 됨",
                        LocalDate.now().plus(1, ChronoUnit.DAYS),
                        null,
                        true,
                        "sample-title",
                        "sample-description"
                ),
                Arguments.of(
                        "종료 시간이 현재보다 나중이면 안 됨",
                        LocalDate.of(2021, 7, 2),
                        LocalDate.now().plus(1, ChronoUnit.DAYS),
                        false,
                        "sample-title",
                        "sample-description"
                ),
                Arguments.of(
                        "isCurrent가 true일 때 endDate는 null이어야 함",
                        LocalDate.of(2018, 2, 7),
                        LocalDate.of(2019, 2, 7),
                        true,
                        "sample=title",
                        "sample-description"
                ),
                Arguments.of(
                        "endDate는 startDate보다 이전일 수 없음",
                        LocalDate.of(2018, 2, 7),
                        LocalDate.of(2018, 2, 6),
                        false,
                        "sample-title",
                        "sample-description"
                ),
                Arguments.of(
                        "title은 null이면 안 됨",
                        LocalDate.of(2018, 2, 7),
                        LocalDate.of(2018, 2, 8),
                        false,
                        null,
                        "sample-description"
                ),
                Arguments.of(
                        "title은 빈 문자열이면 안 됨",
                        LocalDate.of(2018, 2, 7),
                        LocalDate.of(2018, 2, 8),
                        false,
                        "",
                        "sample-description"
                ),
                Arguments.of(
                        "description은 not null",
                        LocalDate.of(2018, 2, 7),
                        LocalDate.of(2018, 2, 8),
                        false,
                        "sample-title",
                        null
                )
        );
    }

    @DisplayName("addSpec - validation error")
    @MethodSource
    @ParameterizedTest(name = "{0}: startDate={1}, endDate={2}, isCurrent={3}, title={4}, description={5}")
    void addSpec_validationFail(String testName,
                                LocalDate startDate,
                                LocalDate endDate,
                                boolean isCurrent,
                                String title,
                                String description) {
        SpecDto specDto = new SpecDto(startDate, endDate, isCurrent, title, description);
        ValidationException validationException =
                catchThrowableOfType(() -> this.specService.addSpec(this.member.getId(), List.of(specDto)), ValidationException.class);
        log.info("ValidationException", validationException);
    }
}