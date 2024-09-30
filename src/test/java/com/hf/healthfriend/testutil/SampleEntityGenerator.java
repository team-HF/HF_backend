package com.hf.healthfriend.testutil;

import com.hf.healthfriend.domain.member.constant.*;
import com.hf.healthfriend.domain.member.entity.Member;

import java.time.LocalDate;

public class SampleEntityGenerator {

    public static Member generateSampleMember(String email) {
        Member member = new Member(email);
        member.setNickname("sample-nickname");
        member.setBirthDate(LocalDate.of(1997, 9, 16));
        member.setGender(Gender.MALE);
        member.setIntroduction("");
        member.setFitnessLevel(FitnessLevel.BEGINNER);
        member.setCompanionStyle(CompanionStyle.GROUP);
        member.setFitnessEagerness(FitnessEagerness.EAGER);
        member.setFitnessObjective(FitnessObjective.BULK_UP);
        member.setFitnessKind(FitnessKind.FUNCTIONAL);
        return member;
    }

    public static Member generateSampleMember(String email, String nickname) {
        Member member = new Member(email);
        member.setNickname(nickname);
        member.setBirthDate(LocalDate.of(1997, 9, 16));
        member.setGender(Gender.MALE);
        member.setIntroduction("");
        member.setFitnessLevel(FitnessLevel.BEGINNER);
        member.setCompanionStyle(CompanionStyle.GROUP);
        member.setFitnessEagerness(FitnessEagerness.EAGER);
        member.setFitnessObjective(FitnessObjective.BULK_UP);
        member.setFitnessKind(FitnessKind.FUNCTIONAL);
        return member;
    }
}
