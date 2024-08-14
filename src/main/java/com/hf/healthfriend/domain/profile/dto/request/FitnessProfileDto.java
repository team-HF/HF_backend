package com.hf.healthfriend.domain.profile.dto.request;

import com.hf.healthfriend.domain.profile.entity.FitnessLevel;
import com.hf.healthfriend.domain.profile.entity.FitnessStyle;
import lombok.Getter;

import java.util.List;

@Getter
public class FitnessProfileDto {
    private FitnessLevel fitnessLevel;
    private List<FitnessStyle> fitnessStyleList;
}

