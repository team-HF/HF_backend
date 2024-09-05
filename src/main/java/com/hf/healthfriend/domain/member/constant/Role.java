package com.hf.healthfriend.domain.member.constant;

public enum Role {
    ROLE_ADMIN("ADMIN"),
    ROLE_MEMBER("MEMBER"),
    ROLE_NON_MEMBER("NON_MEMBER");

    private final String roleName;

    Role(String roleName) {
        this.roleName = roleName;
    }

    public String roleName() {
        return this.roleName;
    }
}
