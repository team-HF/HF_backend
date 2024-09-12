package com.hf.healthfriend.domain.member.repository;

import com.hf.healthfriend.domain.member.entity.Member;
import com.hf.healthfriend.domain.member.exception.MemberNotFoundException;
import com.hf.healthfriend.domain.member.repository.dto.MemberUpdateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepository{
    private final MemberJpaRepository jpaRepository;

    @Override
    public Member save(Member member) {
        return this.jpaRepository.save(member);
    }

    @Override
    public Optional<Member> findById(String id) {
        return this.jpaRepository.findById(id);
    }

    @Override
    public Optional<Member> findByEmail(String email) {
        return this.jpaRepository.findByEmail(email);
    }

    @Override
    public boolean existsById(String id) {
        return this.jpaRepository.existsById(id);
    }

    @Override
    public boolean existsByEmail(String email) {
        return this.jpaRepository.existsByEmail(email);
    }

    @Override
    public Member update(String memberId, MemberUpdateDto updateDto) {
        Member member = this.jpaRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(memberId));

        if (updateDto.getRole() != null) {
            member.setRole(updateDto.getRole());
        }
        if (updateDto.getNickname() != null) {
            member.setNickname(updateDto.getNickname());
        }
        if (updateDto.getProfileImageUrl() != null) {
            member.setProfileImageUrl(updateDto.getProfileImageUrl());
        }
        if (updateDto.getBirthDate() != null) {
            member.setBirthDate(updateDto.getBirthDate());
        }
        if (updateDto.getGender() != null) {
            member.setGender(updateDto.getGender());
        }
        if (updateDto.getIntroduction() != null) {
            member.setIntroduction(updateDto.getIntroduction());
        }
        if (updateDto.getFitnessLevel() != null) {
            member.setFitnessLevel(updateDto.getFitnessLevel());
        }
        if (updateDto.getCompanionStyle() != null) {
            member.setCompanionStyle(updateDto.getCompanionStyle());
        }
        if (updateDto.getFitnessEagerness() != null) {
            member.setFitnessEagerness(updateDto.getFitnessEagerness());
        }
        if (updateDto.getFitnessObjective() != null) {
            member.setFitnessObjective(updateDto.getFitnessObjective());
        }
        if (updateDto.getFitnessKind() != null) {
            member.setFitnessKind(updateDto.getFitnessKind());
        }
        return member;
    }
}
