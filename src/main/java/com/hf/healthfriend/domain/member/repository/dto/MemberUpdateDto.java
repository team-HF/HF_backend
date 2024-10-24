package com.hf.healthfriend.domain.member.repository.dto;

import com.hf.healthfriend.domain.member.constant.*;
import com.hf.healthfriend.domain.member.entity.Member;
import com.hf.healthfriend.global.util.mapping.BeanMapping;
import com.hf.healthfriend.global.util.mapping.MappingAttribute;
import lombok.*;

import java.time.LocalDate;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(toBuilder = true)
@Setter
@Getter
@ToString
@BeanMapping(Member.class)
public class MemberUpdateDto {

    @MappingAttribute(target = "profileImageUrl", setNull = false)
    private String profileImageUrl;

    @MappingAttribute(target = "cd1", setNull = false)
    private String cd1;

    @MappingAttribute(target = "cd2", setNull = false)
    private String cd2;

    @MappingAttribute(target = "cd3", setNull = false)
    private String cd3;

    @MappingAttribute(target = "introduction", setNull = false)
    private String introduction;

    @MappingAttribute(target = "fitnessLevel", setNull = false)
    private FitnessLevel fitnessLevel;

    @MappingAttribute(target = "companionStyle", setNull = false)
    private CompanionStyle companionStyle;

    @MappingAttribute(target = "fitnessEagerness", setNull = false)
    private FitnessEagerness fitnessEagerness;

    @MappingAttribute(target = "fitnessObjective", setNull = false)
    private FitnessObjective fitnessObjective;

    @MappingAttribute(target = "fitnessKind", setNull = false)
    private FitnessKind fitnessKind;
}
