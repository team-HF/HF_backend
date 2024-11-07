package com.hf.healthfriend.domain.member.domain;

import com.hf.healthfriend.domain.member.constant.FitnessLevel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

class TierTest {

    @DisplayName("Tier.create")
    @CsvSource(value = {
            "BEGINNER,0,1", "BEGINNER,1,1", "BEGINNER,4,1", "BEGINNER,5,2",
            "BEGINNER,6,2", "BEGINNER,10,3", "BEGINNER,13,3", "BEGINNER,15,4",
            "BEGINNER,19,4", "BEGINNER,20,5", "BEGINNER,21,5", "BEGINNER,29,5",
            "ADVANCED,20,1", "ADVANCED,21,1", "ADVANCED,24,1", "ADVANCED,25,2",
            "ADVANCED,30,3", "ADVANCED,35,4", "ADVANCED,39,4", "ADVANCED,40,5",
            "ADVANCED,41,5", "ADVANCED,45,5,", "ADVANCED,1354153,5"
    }, delimiter = ',')
    @ParameterizedTest
    void create_success(FitnessLevel fitnessLevel, Long matchedCount, Integer expectedTier) {
        Tier result = Tier.create(fitnessLevel, matchedCount);

        assertThat(result.getFitnessLevel()).isEqualTo(fitnessLevel);
        assertThat(result.getTier()).isEqualTo(expectedTier);
    }
}