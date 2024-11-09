package com.hf.healthfriend.domain.member.repository;

import com.hf.healthfriend.domain.member.entity.Member;
import com.hf.healthfriend.domain.member.repository.dto.ProfileQueryResultDto;
import com.hf.healthfriend.domain.spec.dto.SpecDto;
import com.hf.healthfriend.domain.spec.entity.Spec;
import com.hf.healthfriend.testutil.SampleEntityGenerator;
import com.hf.healthfriend.testutil.TestConfig;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;

@Slf4j
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestConfig.class)
class TestMemberRepository {

    @Autowired
    MemberRepository memberRepository;

    @DisplayName("save - 기본값만 가지고 제대로 Save 되는지 확인")
    @Test
    void save() {
        Member member = SampleEntityGenerator.generateSampleMember("sample@gmail.com");
        assertThatNoException().isThrownBy(() -> this.memberRepository.save(member));
    }

    @DisplayName("findById - 성공 예상")
    @Test
    void findById_successExpected() {
        String loginId = "sample@gmail.com";

        Member member = SampleEntityGenerator.generateSampleMember(loginId);
        this.memberRepository.save(member);

        Member findMember = this.memberRepository.findByLoginId(loginId).orElseThrow(NoSuchElementException::new);

        log.info("findMember={}", findMember);

        assertThat(findMember.getLoginId()).isEqualTo(loginId);
        assertThat(findMember.getEmail()).isEqualTo("sample@gmail.com");
        assertThat(findMember.getPassword()).isNull();
    }

    @DisplayName("findById - 아무것도 찾아오지 못함")
    @Test
    void findById_nothingWillBeFetched() {
        String loginId = "sample-member";

        Member member = SampleEntityGenerator.generateSampleMember(loginId);
        this.memberRepository.save(member);

        Optional<Member> findMemberOp = this.memberRepository.findByLoginId(loginId + "SUFFIX");

        assertThat(findMemberOp).isEmpty();
    }

    @DisplayName("findProfileByMemberId - success")
    @Test
    void findProfileByMemberId_success() {
        // Given
        Member dummyMember1 = SampleEntityGenerator.generateSampleMember("member1@sample.com", "sample");
        Spec spec1 = SampleEntityGenerator.generateSampleSpec(dummyMember1);
        Spec spec2 = SampleEntityGenerator.generateSampleSpec(dummyMember1);
        dummyMember1.addSpec(spec1);
        dummyMember1.addSpec(spec2);
        Member dummyMember2 = SampleEntityGenerator.generateSampleMember("member2@sample.com", "sample");
        Spec irrelevantSpec = SampleEntityGenerator.generateSampleSpec(dummyMember2);
        dummyMember2.addSpec(irrelevantSpec);
        this.memberRepository.save(dummyMember1);
        this.memberRepository.save(dummyMember2);

        // When
        Optional<ProfileQueryResultDto> findProfileOp = this.memberRepository.findProfileByMemberId(dummyMember1.getId());

        log.info("result.memberId={}, result.introduction={}", findProfileOp.get().memberId(), findProfileOp.get().introduction());
        for (SpecDto specDto : findProfileOp.get().specs()) {
            log.info("spec={}", specDto);
        }

        // Then
        assertThat(findProfileOp).isNotEmpty();

        ProfileQueryResultDto findProfile = findProfileOp.get();

        assertThat(findProfile.memberId()).isEqualTo(dummyMember1.getId());
        assertThat(findProfile.introduction()).isEqualTo(dummyMember1.getIntroduction());

        for (SpecDto spec : findProfile.specs()) {
            assertThat(spec.getSpecId()).isIn(spec1.getSpecId(), spec2.getSpecId());
            assertThat(spec.getTitle()).isIn(spec1.getTitle(), spec2.getTitle());
            assertThat(spec.getStartDate()).isIn(spec1.getStartDate(), spec2.getStartDate());
        }
    }
}
