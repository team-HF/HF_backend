package com.hf.healthfriend.domain.member.entity;

import com.hf.healthfriend.domain.member.constant.*;
import com.hf.healthfriend.domain.post.entity.Post;
import com.hf.healthfriend.domain.spec.entity.Spec;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity(name = "members")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Builder
@Setter
@Getter
@ToString
public class Member implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(name = "login_id", unique = true, nullable = false)
    private String loginId;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Role role = Role.ROLE_MEMBER;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "password")
    private String password = null;

    @Column(name = "creation_time")
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime creationTime = LocalDateTime.now();

    @Column(name = "nickname")
    private String nickname;

    @Column(name = "profile_url")
    private String profileImageUrl;

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

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> posts = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Spec> specs = new ArrayList<>();

    public Member(long memberId) {
        this.id = memberId;
    }

    public Member(String email) {
        this.loginId = email;
        this.email = email;
    }

    public Member(String loginId, String email, String password) {
        this.loginId = loginId;
        this.email = email;
        this.password = password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(this.role.name()));
    }

    public GrantedAuthority getAuthority() {
        return new SimpleGrantedAuthority(this.role.name());
    }

    @Override
    public String getUsername() {
        return String.valueOf(this.id);
    }
}
