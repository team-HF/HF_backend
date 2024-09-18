package com.hf.healthfriend.domain.member.repository;

import com.hf.healthfriend.domain.member.entity.Member;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;

@Slf4j
@SpringBootTest
@Transactional
public class TestMemberJpaRepository {

    @Autowired
    MemberJpaRepository memberJpaRepository;

    @DisplayName("save - 기본값만 가지고 제대로 Save 되는지 확인")
    @Test
    void save() {
        Member member = new Member("sample-member", "sample@gmail.com", null);
        assertThatNoException().isThrownBy(() -> this.memberJpaRepository.save(member));
    }

    @DisplayName("findById - 성공 예상")
    @Test
    void findById_successExpected() {
        String loginId = "sample-member";

        Member member = new Member(loginId, "sample@gmail.com", null);
        this.memberJpaRepository.save(member);

        Member findMember = this.memberJpaRepository.findByLoginId(loginId).orElseThrow(NoSuchElementException::new);

        log.info("findMember={}", findMember);

        assertThat(findMember.getLoginId()).isEqualTo(loginId);
        assertThat(findMember.getEmail()).isEqualTo("sample@gmail.com");
        assertThat(findMember.getPassword()).isNull();
    }

    @DisplayName("findById - 아무것도 찾아오지 못함")
    @Test
    void findById_nothingWillBeFetched() {
        String loginId = "sample-member";

        Member member = new Member(loginId, "sample@gmail.com", null);
        this.memberJpaRepository.save(member);

        Optional<Member> findMemberOp = this.memberJpaRepository.findByLoginId(loginId + "SUFFIX");

        assertThat(findMemberOp).isEmpty();
    }
}
