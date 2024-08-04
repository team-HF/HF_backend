package com.hf.healthfriend.domain.member.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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

    private String nickName;

    private String email;

    private String password;

    private String refreshToken;

    private double latitude;

    private double longitude;

    @Builder
    public Member(Role role, String nickName, String email, String password, String refreshToken, double latitude, double longitude) {
        this.role = role;
        this.nickName = nickName;
        this.email = email;
        this.password = password;
        this.refreshToken = refreshToken;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public void updateRefreshToken(String refreshToken){
        this.refreshToken = refreshToken;
    }
}
