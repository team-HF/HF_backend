package com.hf.healthfriend.domain.member.accesscontrol;

import com.hf.healthfriend.auth.accesscontrol.AccessControlTrigger;
import com.hf.healthfriend.auth.accesscontrol.AccessController;
import com.hf.healthfriend.domain.member.constant.Role;
import com.hf.healthfriend.domain.member.repository.MemberJpaRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthentication;

import java.util.List;

@Slf4j
@AccessController
@RequiredArgsConstructor
public class MemberAccessController {
    private final MemberJpaRepository memberJpaRepository;

    @AccessControlTrigger(path = "/members/{memberId}", method = "GET")
    public boolean controlAccessToMemberInfo(BearerTokenAuthentication authentication, HttpServletRequest request) {
        return accessControl(authentication, request);
    }

    @AccessControlTrigger(path = "/members/{memberId}", method = "PATCH")
    public boolean accessControlForUpdateMember(BearerTokenAuthentication authentication, HttpServletRequest request) {
        return accessControl(authentication, request);
    }

    private boolean accessControl(BearerTokenAuthentication authentication, HttpServletRequest request) {
        List<String> authorities = authentication.getAuthorities().stream()
                .map((GrantedAuthority::getAuthority))
                .toList();
        if (authorities.isEmpty() || authorities.contains(Role.ROLE_NON_MEMBER.name())) {
            return false;
        } else if (authorities.contains(Role.ROLE_ADMIN.name())) {
            return true;
        }

        String path = request.getRequestURI();
        String resourceMemberId = path.substring(path.lastIndexOf('/') + 1);

        if (!memberJpaRepository.existsById(resourceMemberId)) {
            return true;
        }

        if (log.isTraceEnabled()) {
            log.trace("path={}", path);
            log.trace("pathVariable={}", resourceMemberId);
            log.trace("authentication.getName()={}", authentication.getName());
            log.trace("pathVariable.equals(authentication.getName())={}", resourceMemberId.equals(authentication.getName()));
        }
        return resourceMemberId.equals(authentication.getName());
    }
}
