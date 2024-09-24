package com.hf.healthfriend.domain.member.repository;

import com.hf.healthfriend.domain.member.entity.Member;
import com.hf.healthfriend.domain.member.repository.dto.MemberUpdateDto;

import java.util.Optional;

public interface MemberRepository {

    Member save(Member member);

    Optional<Member> findById(Long id);

    Optional<Member> findByLoginId(String loginId);

    Optional<Member> findByEmail(String email);

    boolean existsById(Long id);

    boolean existsByLoginId(String loginId);

    boolean existsByEmail(String email);

    Member update(Long memberId, MemberUpdateDto updateDto);
}
