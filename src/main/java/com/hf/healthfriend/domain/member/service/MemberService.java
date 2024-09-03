package com.hf.healthfriend.domain.member.service;

import com.hf.healthfriend.domain.member.dto.request.MemberCreationRequestDto;
import com.hf.healthfriend.domain.member.dto.response.MemberCreationResponseDto;
import com.hf.healthfriend.domain.member.entity.Member;
import com.hf.healthfriend.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {
    private final MemberRepository memberRepository;

    public MemberCreationResponseDto createMemberIfNotExists(MemberCreationRequestDto dto) {
        if (this.memberRepository.existsById(dto.getId())) {
            return MemberCreationResponseDto.notCreated(dto.getId());
        }

        Member newMember = Member.builder()
                .id(dto.getId())
                .email(dto.getEmail())
                .password(dto.getPassword())
                .build();
        Member saved = this.memberRepository.save(newMember);
        return MemberCreationResponseDto.of(saved);
    }
}
