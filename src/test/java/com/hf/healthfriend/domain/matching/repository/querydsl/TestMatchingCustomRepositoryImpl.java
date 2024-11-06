package com.hf.healthfriend.domain.matching.repository.querydsl;

import com.hf.healthfriend.domain.matching.constant.MatchingFetchType;
import com.hf.healthfriend.domain.matching.constant.MatchingStatus;
import com.hf.healthfriend.domain.matching.constant.MatchingStatusCondition;
import com.hf.healthfriend.domain.matching.dto.response.MatchingListResponseDto;
import com.hf.healthfriend.domain.matching.entity.Matching;
import com.hf.healthfriend.domain.member.entity.Member;
import com.hf.healthfriend.testutil.SampleEntityGenerator;
import com.hf.healthfriend.testutil.TestConfig;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestConfig.class)
@Slf4j
class TestMatchingCustomRepositoryImpl {

    @Autowired
    MatchingCustomRepositoryImpl matchingCustomRepository;

    @Autowired
    EntityManager em;

    Member mainMember;
    Map<String, Member> dummyMembersByKey;
    List<Matching> dummyMatchings;

    static final int REQUESTER_NUM = 7;
    static final int REQUEST_TARGET_NUM = 7;

    @BeforeEach
    void initialData() throws NoSuchFieldException, IllegalAccessException {
        Member mainMember = SampleEntityGenerator.generateSampleMember("main@gmail.com", "main");
        this.em.persist(mainMember);

        Map<String, Member> dummyMembers = generateDummyMembers();
        List<Matching> matchings = generateMatchings(mainMember, dummyMembers);

        this.mainMember = mainMember;
        this.dummyMembersByKey = dummyMembers;
        this.dummyMatchings = matchings;
    }

    private Map<String, Member> generateDummyMembers() {
        Map<String, Member> dummyMembers = new HashMap<>();
        for (int i = 0; i < REQUESTER_NUM; i++) {
            putSingleDummyMember(i + 1, "er", dummyMembers);
        }
        for (int i = 0; i < REQUEST_TARGET_NUM; i++) {
            putSingleDummyMember(i + 1 + REQUESTER_NUM, "ee", dummyMembers);
        }

        return Collections.unmodifiableMap(dummyMembers);
    }

    private void putSingleDummyMember(int number, String prefix, Map<String, Member> map) {
        Member member =
                SampleEntityGenerator.generateSampleMember("dummy" + number + "@gmail.com", "requester" + number);
        this.em.persist(member);
        map.put(prefix + number, member);
    }

    private List<Matching> generateMatchings(Member mainMember, Map<String, Member> memberMap)
            throws NoSuchFieldException, IllegalAccessException {

        int iterCount = 0;

        MatchingStatus[] statusOffsets = {
                MatchingStatus.PENDING,
                MatchingStatus.ACCEPTED,
                MatchingStatus.REJECTED,
                MatchingStatus.FINISHED
        };

        Field statusField = Matching.class.getDeclaredField("status");
        statusField.setAccessible(true);
        List<Matching> matchings = new ArrayList<>();
        for (Map.Entry<String, Member> entry : memberMap.entrySet()) {
            iterCount++;
            Matching matching = new Matching(
                    entry.getKey().startsWith("ee") ? mainMember : entry.getValue(),
                    entry.getKey().startsWith("ee") ? entry.getValue() : mainMember,
                    "sample-place",
                    "sample-address",
                    LocalDateTime.now().plusDays(iterCount)
            );

            statusField.set(matching, statusOffsets[iterCount % statusOffsets.length]);

            this.em.persist(matching);
            matchings.add(matching);
            log.info("matching id={}", matching.getMatchingId());
        }
        statusField.setAccessible(false);

        matchings.sort((m1, m2) -> {
            if (m1.getMeetingTime().isBefore(m2.getMeetingTime())) {
                return 1;
            } else if (m1.getMeetingTime().isAfter(m2.getMeetingTime())) {
                return -1;
            } else {
                return 0;
            }
        });
        return Collections.unmodifiableList(matchings);
    }

    static Stream<Arguments> findByMemberIdWithConditions_success() {
        return Stream.of(
                // Pagination
                Arguments.of(
                        "Pagination: 페이지 1",
                        MatchingFetchType.ALL,
                        MatchingStatusCondition.ALL,
                        PageRequest.of(0, 4)
                ),
                Arguments.of(
                        "Pagination: 페이지 2",
                        MatchingFetchType.ALL,
                        MatchingStatusCondition.ALL,
                        PageRequest.of(1, 4)
                ),
                Arguments.of(
                        "Pagination: 마지막 페이지",
                        MatchingFetchType.ALL,
                        MatchingStatusCondition.ALL,
                        PageRequest.of((REQUESTER_NUM + REQUEST_TARGET_NUM) / 4, 4)
                ),
                Arguments.of(
                        "Pagination: 범위 밖을 벗어난 페이지",
                        MatchingFetchType.ALL,
                        MatchingStatusCondition.ALL,
                        PageRequest.of(1361521542, 4)
                ),

                // Fetch Type
                Arguments.of(
                        "Fetch Type: 내가 요청한 것",
                        MatchingFetchType.WHAT_I_RECEIVED,
                        MatchingStatusCondition.ALL,
                        PageRequest.of(0, 4)
                ),
                Arguments.of(
                        "Fetch Type: 내가 요청받은 것",
                        MatchingFetchType.WHAT_I_REQUESTED,
                        MatchingStatusCondition.ALL,
                        PageRequest.of(0, 4)
                ),

                // Page size
                Arguments.of(
                        "Page size: 사이즈 5",
                        MatchingFetchType.ALL,
                        MatchingStatusCondition.ALL,
                        PageRequest.of(0, 5)
                ),
                Arguments.of(
                        "Page size: 사이즈 10",
                        MatchingFetchType.ALL,
                        MatchingStatusCondition.ALL,
                        PageRequest.of(0, 10)
                ),

                // MatchingStatusCondition
                Arguments.of(
                        "MatchingStatusCondition: 진행 중",
                        MatchingFetchType.ALL,
                        MatchingStatusCondition.IN_PROGRESS,
                        PageRequest.of(0, 4)
                ),
                Arguments.of(
                        "MatchingStatusCondition: 중지된 매칭",
                        MatchingFetchType.ALL,
                        MatchingStatusCondition.HALTED,
                        PageRequest.of(0, 4)
                ),
                Arguments.of(
                        "MatchingStatusCondition: 종료",
                        MatchingFetchType.ALL,
                        MatchingStatusCondition.FINISHED,
                        PageRequest.of(0, 4)
                )
        );
    }

    @DisplayName("findByMemberIdWithConditions - success")
    @MethodSource
    @ParameterizedTest(name = "{0} - fetchType={1}, condition={2}, page={3}")
    void findByMemberIdWithConditions_success(String parameterizedName,
                                              MatchingFetchType fetchType,
                                              MatchingStatusCondition condition,
                                              Pageable page) {
        // Given
        List<Matching> filteredMatchings = filterBySearchCondtition(this.dummyMatchings, fetchType, condition);
        List<Matching> expectedMatchingList =
                subListWithoutOverflow(filteredMatchings, (int) page.getOffset(), (int) page.getOffset() + page.getPageSize());

        // When
        Page<MatchingListResponseDto> result =
                this.matchingCustomRepository.findByMemberIdWithConditions(this.mainMember.getId(), fetchType, condition, page);

        // Then
        log.info("expected matching ids={}", expectedMatchingList.stream().map(Matching::getMatchingId).toList());
        log.info("result matching ids={}", result.getContent().stream().map(MatchingListResponseDto::matchingId).toList());

        assertThat(result.getTotalElements()).isEqualTo(filteredMatchings.size());
        assertThat(result.getContent().stream().map(MatchingListResponseDto::matchingId))
                .containsExactly(expectedMatchingList.stream().map(Matching::getMatchingId).toArray(Long[]::new));
    }

    private List<Matching> filterBySearchCondtition(List<Matching> original,
                                                    MatchingFetchType fetchType,
                                                    MatchingStatusCondition condition) {
        return original.stream()
                .filter((m) -> fetchType == MatchingFetchType.ALL
                        || (fetchType == MatchingFetchType.WHAT_I_RECEIVED && m.getTargetMember().getId().equals(this.mainMember.getId()))
                        || (fetchType == MatchingFetchType.WHAT_I_REQUESTED && m.getRequester().getId().equals(this.mainMember.getId())))
                .filter((m) -> condition == MatchingStatusCondition.ALL
                        || condition.getCorrespondingStatus().contains(m.getStatus()))
                .toList();
    }

    private <T> List<T> subListWithoutOverflow(List<T> original, int fromIndex, int endIndex) {
        if (fromIndex >= original.size()) {
            return List.of();
        }
        return original.subList(fromIndex, Math.min(endIndex, original.size()));
    }
}