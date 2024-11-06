package com.hf.healthfriend.domain.matching.controller.schema;

import com.hf.healthfriend.domain.matching.dto.response.MatchingListResponseDto;
import com.hf.healthfriend.domain.matching.dto.response.PageResponseDto;
import com.hf.healthfriend.global.spec.ApiBasicResponse;

public class PaginatedMatchingListResponseSchema extends ApiBasicResponse<PageResponseDto<MatchingListResponseDto>> {
}
