package com.hf.healthfriend.domain.member.repository;

import com.hf.healthfriend.domain.member.entity.Member;
import com.hf.healthfriend.domain.member.repository.dto.MemberUpdateDto;

import java.util.Optional;

public interface MemberRepository {

    Member save(Member member);

    Optional<Member> findById(String id);

    Optional<Member> findByEmail(String email);

    boolean existsById(String id);

    boolean existsByEmail(String email);

    Member update(String memberId, MemberUpdateDto updateDto);
}
