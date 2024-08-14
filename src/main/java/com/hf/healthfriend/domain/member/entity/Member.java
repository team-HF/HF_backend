package com.hf.healthfriend.domain.member.entity;

import com.hf.healthfriend.domain.profile.entity.Profile;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "members")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Member {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private Role role;

    private String email;

    private String password;

    private String refreshToken;

    @OneToOne
    @JoinColumn(name = "profile_id")
    private Profile profile;

    @Builder
    public Member(Role role, String email, String password, String refreshToken, Profile profile) {
        this.role = role;
        this.email = email;
        this.password = password;
        this.refreshToken = refreshToken;
        this.profile = profile;
    }

    public void updateRefreshToken(String refreshToken){
        this.refreshToken = refreshToken;
    }
}
