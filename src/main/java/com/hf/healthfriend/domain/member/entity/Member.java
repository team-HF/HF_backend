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
@Setter // TODO: Setter를 없애고 엔티티 수정 코드는 udpate~ 메소드로 대체해야 함
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
    @Column(name = "role", nullable = false)
    private Role role = Role.ROLE_MEMBER;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "password")
    private String password = null;

    @Column(name = "creation_time")
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime creationTime = LocalDateTime.now();

    @Column(name = "nickname", nullable = false)
    private String nickname;

    @Column(name = "profile_url")
    private String profileImageUrl;

    @Column(name = "location", nullable = false)
    private String location;

    @Column(name = "birth_date", nullable = false)
    @Temporal(TemporalType.DATE)
    private LocalDate birthDate;

    @Column(name = "gender", nullable = false)
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(name = "introduction", nullable = false)
    private String introduction;

    @Column(name = "fitness_level", nullable = false)
    @Enumerated(EnumType.STRING)
    private FitnessLevel fitnessLevel;

    @Column(name = "companion_style", nullable = false)
    @Enumerated(EnumType.STRING)
    private CompanionStyle companionStyle;

    @Column(name = "fitness_eagerness", nullable = false)
    @Enumerated(EnumType.STRING)
    private FitnessEagerness fitnessEagerness;

    @Column(name = "fitness_objective", nullable = false)
    @Enumerated(EnumType.STRING)
    private FitnessObjective fitnessObjective;

    @Column(name = "fitness_kind", nullable = false)
    @Enumerated(EnumType.STRING)
    private FitnessKind fitnessKind;

    @Column(name = "is_deleted")
    private boolean isDeleted = false;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> posts = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Spec> specs = new ArrayList<>();

    public Member(long memberId) {
        this.id = memberId;
    }

    public Member(String loginId) {
        this.loginId = loginId;
        this.email = loginId;
    }

    public Member(String loginId, String password) {
        this.loginId = loginId;
        this.email = loginId;
        this.password = password;
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

    public void delete() {
        this.isDeleted = true;
    }
}
