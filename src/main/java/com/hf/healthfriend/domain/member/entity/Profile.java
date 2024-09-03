package com.hf.healthfriend.domain.member.entity;

import com.hf.healthfriend.domain.member.constant.*;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@ToString(exclude = "member")
public class Profile {

    @Id
    @OneToOne
    @MapsId
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(name = "nickname")
    private String nickname;

    @Column(name = "birth_date")
    @Temporal(TemporalType.DATE)
    private LocalDate birthDate;

    @Column(name = "gender")
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(name = "introduction")
    private String introduction;

    @Column(name = "fitness_level")
    @Enumerated(EnumType.STRING)
    private FitnessLevel fitnessLevel;

    @Column(name = "companion_style")
    @Enumerated(EnumType.STRING)
    private CompanionStyle companionStyle;

    @Column(name = "fitness_eagerness")
    @Enumerated(EnumType.STRING)
    private FitnessEagerness fitnessEagerness;

    @Column(name = "fitness_objective")
    @Enumerated(EnumType.STRING)
    private FitnessObjective fitnessObjective;

    @Column(name = "fitness_kind")
    @Enumerated(EnumType.STRING)
    private FitnessKind fitnessKind;
}
