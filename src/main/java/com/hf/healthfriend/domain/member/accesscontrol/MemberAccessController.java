package com.hf.healthfriend.domain.member.accesscontrol;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hf.healthfriend.auth.accesscontrol.AccessControlTrigger;
import com.hf.healthfriend.auth.accesscontrol.AccessController;
import com.hf.healthfriend.domain.member.constant.Role;
import com.hf.healthfriend.domain.member.repository.MemberRepository;
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
    private final MemberRepository memberRepository;
    private final ObjectMapper objectMapper;

//    @AccessControlTrigger(path = "/members", method = "POST")
//    public boolean preventSignUpByOtherClient(BearerTokenAuthentication authentication, HttpServletRequest request)
//            throws IOException {
//        MemberCreationRequestDto body;
//        try (BufferedReader br = request.getReader()) {
//            StringBuilder sb = new StringBuilder();
//            String line;
//            while ((line = br.readLine()) != null) { // 아마 한 줄에 데이터가 다 올 것이므로 필요는 없겠지만... 일단 해 둔다
//                sb.append(line).append('\n');
//            }
//
//            body = this.objectMapper.readValue(sb.toString(), MemberCreationRequestDto.class);
//        } catch (StreamReadException | DatabindException e) { // TODO: 각각 예외에 대해 정리하고 어떻게 처리해야 하는지 조사해야 함
//            throw e; // TODO: 400 Bad Request 응답이 가도록 조치를 취해야 함 (AccessControlFilter에서 처리하면 될 듯)
//        }
//
//        String authenticatedPrincipal = authentication.getName();
//
//        if (log.isTraceEnabled()) {
//            log.trace("authentication.name={}", authenticatedPrincipal);
//            log.trace("MemberCreationRequestDto.id={}", body.getId());
//        }
//
//        // Access Token으로 인증한 사용자가 정말 자신의 아이디로 된 계정을 생성하려고 했는지 검증
//        return body.getId().equals(authenticatedPrincipal);
//    }

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
