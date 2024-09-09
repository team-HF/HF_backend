package com.hf.healthfriend.domain.member.service;

import com.hf.healthfriend.domain.member.dto.MemberDto;
import com.hf.healthfriend.domain.member.dto.MemberUpdateDto;
import com.hf.healthfriend.domain.member.dto.request.MemberCreationRequestDto;
import com.hf.healthfriend.domain.member.dto.response.MemberCreationResponseDto;
import com.hf.healthfriend.domain.member.entity.Member;
import com.hf.healthfriend.domain.member.exception.DuplicateMemberCreationException;
import com.hf.healthfriend.domain.member.exception.MemberNotFoundException;
import com.hf.healthfriend.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {
    private final MemberRepository memberRepository;

    /**
     * MemberCreationRequestDto에 있는 데이터를 가지고 새로운 Member를 생성한다.
     * @param dto 생성할 Member의 정보가 담겨 있는 DTO 객체. id는 email과 같으며 password는 필요없다. (OAuth 2.0을 사용하는 도메인
     *            정책 때문)
     * @return 생성된 회원의 정보가 담긴 DTO 객체
     * @throws DuplicateMemberCreationException 이미 생성된 회원을 다시 생성하려고 할 경우 발생
     */
    public MemberCreationResponseDto createMember(MemberCreationRequestDto dto)
            throws DuplicateMemberCreationException {
        if (this.memberRepository.existsById(dto.getId())) {
            throw new DuplicateMemberCreationException(dto.getId());
        }

        Member newMember = new Member(dto.getId());
        BeanUtils.copyProperties(dto, newMember);
        if (log.isDebugEnabled()) {
            log.debug("newMember={}", newMember);
        }
        Member saved = this.memberRepository.save(newMember);
        return MemberCreationResponseDto.of(saved);
    }

    public boolean isMemberExists(String memberId) {
        return this.memberRepository.existsById(memberId);
    }

    public MemberDto findMember(String memberId) throws MemberNotFoundException {
        return MemberDto.of(this.memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(memberId)));
    }

    public MemberDto updateMember(String memberId, MemberUpdateDto updateDto) throws MemberNotFoundException {
        Member updatedMember = this.memberRepository.update(memberId, updateDto);
        return MemberDto.of(updatedMember);
    }
}
