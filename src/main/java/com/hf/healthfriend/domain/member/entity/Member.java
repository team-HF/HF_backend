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
import lombok.*;

@Entity(name = "members")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
@ToString
public class Member {

    @Id
    @Column(name = "member_id")
    private String id;

    @Enumerated(EnumType.STRING)
    private Role role;

    private String email;

    private String password;
}
