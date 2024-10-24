package com.hf.healthfriend.domain.member.repository;

import com.hf.healthfriend.domain.member.entity.Member;
import com.hf.healthfriend.domain.member.exception.MemberNotFoundException;
import com.hf.healthfriend.domain.member.repository.dto.MemberUpdateDto;
import com.hf.healthfriend.global.util.mapping.BeanMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepository{
    private final MemberJpaRepository jpaRepository;
    private final BeanMapper beanMapper;

    @Override
    public Member save(Member member) {
        log.debug("new member={}", member);
        return this.jpaRepository.save(member);
    }

    @Override
    public Optional<Member> findById(Long id) {
        return this.jpaRepository.findById(id);
    }

    @Override
    public Optional<Member> findByLoginId(String loginId) {
        return this.jpaRepository.findByLoginId(loginId);
    }

    @Override
    public Optional<Member> findByEmail(String email) {
        return this.jpaRepository.findByEmail(email);
    }

    @Override
    public boolean existsById(Long id) {
        return this.jpaRepository.existsById(id);
    }

    @Override
    public boolean existsByLoginId(String loginId) {
        return this.jpaRepository.existsByLoginId(loginId);
    }

    @Override
    public boolean existsByEmail(String email) {
        return this.jpaRepository.existsByEmail(email);
    }

    @Override
    public Member update(Long memberId, MemberUpdateDto updateDto) {
        Member member = this.jpaRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(memberId));

        this.beanMapper.copyProperties(updateDto, member);

        return member;
    }
}
