package com.hf.healthfriend.domain.member.repository.querydsl;

import com.hf.healthfriend.domain.member.dto.request.MembersRecommendRequest;
import com.hf.healthfriend.domain.member.dto.response.MemberRecommendResponse;
import com.hf.healthfriend.domain.member.dto.response.MemberSearchResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MemberCustomRepository {
    public List<MemberRecommendResponse> recommendMembers(MembersRecommendRequest request, Pageable pageable);
    public List<MemberSearchResponse> searchMembers(String keyword, Pageable pageable);
}
