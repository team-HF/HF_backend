package com.hf.healthfriend.domain.member.domain;

import com.hf.healthfriend.domain.member.constant.FitnessLevel;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@ToString
public class Tier {
    private static final Long[] TIER_MILESTONE = {
            5L, 9L, 14L, 20L
    };

    private final FitnessLevel fitnessLevel;
    private final Integer tier;

    public static Tier create(FitnessLevel fitnessLevel, Long matchedCount) {
        if (fitnessLevel == FitnessLevel.ADVANCED) {
            matchedCount -= TIER_MILESTONE[TIER_MILESTONE.length - 1];
        }

        int tier = 1;
        for (Long milestone : TIER_MILESTONE) {
            if (milestone > matchedCount) {
                return new Tier(fitnessLevel, tier);
            }
            tier++;
        }
        return new Tier(fitnessLevel, TIER_MILESTONE.length + 1);
    }
}
