package com.hf.healthfriend.domain.profile.entity;

import com.hf.healthfriend.domain.member.entity.Member;
import com.hf.healthfriend.domain.profile.dto.request.FitnessProfileDto;
import com.hf.healthfriend.domain.profile.dto.request.NicknameDto;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity(name = "profiles")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Profile {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "profile_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private FitnessLevel fitnessLevel;

    @ElementCollection(targetClass = FitnessStyle.class, fetch = FetchType.LAZY)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "profile_fitness_styles",
            joinColumns = @JoinColumn(name = "profile_id"))
    private List<FitnessStyle> fitnessStyleList = new ArrayList<>();

    private String nickName;

    private Long nicknameChangeCount;

    private String profileImageUrl;

    private double latitude;

    private double longitude;

    @OneToOne(mappedBy = "profile", orphanRemoval = true, fetch = FetchType.LAZY)
    private Member member;


    @Builder
    public Profile(FitnessLevel fitnessLevel, List<FitnessStyle> fitnessStyleList, String nickName, Long nicknameChangeCount, String profileImageUrl, double latitude, double longitude) {
        this.fitnessLevel = fitnessLevel;
        this.fitnessStyleList = fitnessStyleList;
        this.nickName = nickName;
        this.nicknameChangeCount = nicknameChangeCount;
        this.profileImageUrl = profileImageUrl;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public void updateFitnessLevel(FitnessProfileDto fitnessProfileDto){
        this.fitnessLevel = fitnessProfileDto.getFitnessLevel();
    }

    public void updateFitnessStyle(FitnessProfileDto fitnessProfileDto){
        this.fitnessStyleList = fitnessProfileDto.getFitnessStyleList();
    }

    public void updateNickName(NicknameDto nicknameDto){
        this.nickName = nicknameDto.getNewNickname();
        this.nicknameChangeCount++;
    }
}
