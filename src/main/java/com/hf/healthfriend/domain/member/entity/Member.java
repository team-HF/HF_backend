package com.hf.healthfriend.domain.member.entity;

import com.hf.healthfriend.domain.member.constant.Role;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity(name = "members")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
@ToString
public class Member implements UserDetails {

    @Id
    @Column(name = "member_id")
    private String id;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Role role = Role.ROLE_USER;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password = null;

    @Column(name = "creation_time")
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime creationTime = LocalDateTime.now();

    @OneToOne(mappedBy = "member", fetch = FetchType.LAZY)
    private Profile profile = null;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(this.role.name()));
    }

    public GrantedAuthority getAuthority() {
        return new SimpleGrantedAuthority(this.role.name());
    }

    @Override
    public String getUsername() {
        return this.id;
    }
}
