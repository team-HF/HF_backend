package com.hf.healthfriend.domain.member.repository.querydsl;

import com.hf.healthfriend.domain.member.dto.request.MembersSearchRequest;
import com.hf.healthfriend.domain.member.dto.response.MemberRecommendResponse;
import com.hf.healthfriend.domain.member.dto.response.MemberSearchResponse;
import com.hf.healthfriend.domain.member.entity.Member;
import com.hf.healthfriend.domain.member.repository.dto.MemberUpdateDto;
import com.hf.healthfriend.domain.member.repository.dto.ProfileQueryResultDto;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface MemberCustomRepository {

    public Optional<Member> findByMemberId(Long memberId);

    public List<MemberSearchResponse> searchMembers(String keyword, MembersSearchRequest request, Pageable pageable);

    public Member update(Long memberId, MemberUpdateDto updateDto);

    public Optional<ProfileQueryResultDto> findProfileByMemberId(Long memberId);
}
