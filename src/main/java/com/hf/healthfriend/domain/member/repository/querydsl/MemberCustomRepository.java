package com.hf.healthfriend.domain.member.repository.querydsl;

import com.hf.healthfriend.domain.member.dto.request.MembersRecommendRequest;
import com.hf.healthfriend.domain.member.dto.response.MemberRecommendResponse;
import com.hf.healthfriend.domain.member.entity.Member;
import com.hf.healthfriend.domain.member.dto.response.MemberSearchResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface MemberCustomRepository {
    public List<MemberRecommendResponse> recommendMembers(MembersRecommendRequest request, Pageable pageable);

    public Optional<Member> findByMemberId(Long memberId);

    public List<MemberSearchResponse> searchMembers(String keyword, Pageable pageable);
}
