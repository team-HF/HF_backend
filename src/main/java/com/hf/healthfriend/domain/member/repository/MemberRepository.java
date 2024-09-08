package com.hf.healthfriend.domain.member.repository;

import com.hf.healthfriend.domain.member.dto.MemberUpdateDto;
import com.hf.healthfriend.domain.member.entity.Member;

import java.util.Optional;

public interface MemberRepository {

    Member save(Member member);

    Optional<Member> findById(String id);

    Optional<Member> findByEmail(String email);

    boolean existsById(String id);

    boolean existsByEmail(String email);

    Member update(String memberId, MemberUpdateDto updateDto);
}
