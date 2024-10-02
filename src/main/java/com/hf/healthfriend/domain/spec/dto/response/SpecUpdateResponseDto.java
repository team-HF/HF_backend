package com.hf.healthfriend.domain.spec.dto.response;

import java.util.List;

public record SpecUpdateResponseDto(
        List<Long> insertedSpecIds,
        List<Long> updatedSpecIds,
        List<Long> deletedSpecIds
) {
}
