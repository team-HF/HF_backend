package com.hf.healthfriend.domain.coupon.event.message;

import com.hf.healthfriend.domain.member.domain.Tier;

public record MatchingEventMessage(
        Long memberId,
        Tier beforeTier,
        Tier afterTier
) {
}
