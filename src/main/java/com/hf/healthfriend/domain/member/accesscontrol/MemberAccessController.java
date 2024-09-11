package com.hf.healthfriend.domain.member.accesscontrol;

import com.hf.healthfriend.auth.accesscontrol.AccessControlTrigger;
import com.hf.healthfriend.auth.accesscontrol.AccessController;
import com.hf.healthfriend.domain.member.constant.Role;
import com.hf.healthfriend.domain.member.repository.MemberRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Part;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthentication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

@Slf4j
@AccessController
@RequiredArgsConstructor
public class MemberAccessController {
    private static final int BUFFER_SIZE = 1024;

    private final MemberRepository memberRepository;

    @AccessControlTrigger(path = "/members", method = "POST")
    public boolean preventSignUpByOtherClient(BearerTokenAuthentication authentication, HttpServletRequest request)
            throws ServletException, IOException {
        Part idPart = request.getPart("id");
        String id;

        try (BufferedReader br = new BufferedReader(new InputStreamReader(idPart.getInputStream()))) {
            StringBuilder sb = new StringBuilder();
            char[] buffer = new char[BUFFER_SIZE];
            while (br.read(buffer) != -1) {
                sb.append(String.valueOf(buffer));
            }

            id = sb.toString().trim();
        }
        log.trace("id={}", id);

        String authenticatedMemberId = authentication.getName(); // null이면 이미 401이 났을 테니 null이 아닐 것

        return authenticatedMemberId.equals(id.trim());
    }

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

        if (!memberRepository.existsById(resourceMemberId)) {
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
