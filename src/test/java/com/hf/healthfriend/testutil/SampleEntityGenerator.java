package com.hf.healthfriend.testutil;

import com.hf.healthfriend.domain.member.constant.*;
import com.hf.healthfriend.domain.member.entity.Member;

import java.time.LocalDate;

public class SampleEntityGenerator {

    public static Member generateSampleMember(String email) {
        return generateSampleMember(email, "샘플닉네임");
    }

    public static Member generateSampleMember(String email, String nickname) {
        return generateSampleMember(email, nickname, "010-1234-1234");
    }

    public static Member generateSampleMember(String email, String nickname, String phoneNumber) {
        Member member = new Member(email);
        member.setNickname(nickname);
        member.setBirthDate(LocalDate.of(1997, 9, 16));
        member.setName("김샘플");
        member.setLocation("서울시 영등포구");
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
