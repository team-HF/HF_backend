package com.hf.healthfriend.domain.coupon.event.handler;

import com.hf.healthfriend.domain.coupon.dto.request.GrantMatchingCouponDto;
import com.hf.healthfriend.domain.coupon.event.message.MatchingEventMessage;
import com.hf.healthfriend.domain.coupon.service.CouponService;
import com.hf.healthfriend.domain.member.domain.Tier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.temporal.ChronoUnit;
import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class MatchingEventHandler {
    // Key는 달성한 고수 레벨
    // Value 배열의 index 0은 매칭 신청권의 매칭 횟수, index 1은 유효 기간(일)
    private static final Map<Integer, int[]> REWARD_PER_LEVEL = Map.of(
            3, new int[]{2, 7},
            4, new int[]{3, 15},
            5, new int[]{5, 30}
    );

    private final CouponService couponService;

    @EventListener
    public void processMatchingFinishEvent(MatchingEventMessage message) {
        Tier beforeTier = message.beforeTier();
        Tier afterTier = message.afterTier();

        if (beforeTier.getTier().equals(afterTier.getTier())) {
            return;
        }

        int[] reward = REWARD_PER_LEVEL.get(afterTier.getTier());
        this.couponService.grantCoupon(GrantMatchingCouponDto.builder()
                .receiverId(message.memberId())
                .grantedMatchingCount(reward[0])
                .validTimeAmount(reward[1])
                .validTimeUnit(ChronoUnit.DAYS)
                .achievedLevel(afterTier.getTier())
                .build()
        );
    }
}
