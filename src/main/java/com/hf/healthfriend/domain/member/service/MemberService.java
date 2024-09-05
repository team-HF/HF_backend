package com.hf.healthfriend.domain.member.service;

import com.hf.healthfriend.domain.member.dto.MemberDto;
import com.hf.healthfriend.domain.member.dto.request.MemberCreationRequestDto;
import com.hf.healthfriend.domain.member.dto.response.MemberCreationResponseDto;
import com.hf.healthfriend.domain.member.entity.Member;
import com.hf.healthfriend.domain.member.exception.NoSuchMemberException;
import com.hf.healthfriend.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {
    private final MemberRepository memberRepository;

    // TODO: Specification (JavaDoc) 작성해야 함 - 이미 있는 사용자를 생성하려고 할 경우 어떻게 되는지도 고려해야 함
    public MemberCreationResponseDto createMember(MemberCreationRequestDto dto) {
        if (this.memberRepository.existsById(dto.getId())) {
            return MemberCreationResponseDto.notCreated(dto.getId());
        }

        Member newMember = new Member(dto.getId(), dto.getEmail(), dto.getPassword());
        Member saved = this.memberRepository.save(newMember);
        return MemberCreationResponseDto.of(saved);
    }

    public MemberDto findMember(String memberId) throws NoSuchMemberException {
        return MemberDto.of(this.memberRepository.findById(memberId)
                .orElseThrow(() -> new NoSuchMemberException(memberId)));
    }
}
