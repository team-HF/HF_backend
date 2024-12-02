package com.hf.healthfriend.global.websocket;

import java.security.Principal;

public record CustomPrincipal(String memberId) implements Principal {

    @Override
    public String getName() {
        return this.memberId;
    }
}
