package com.hf.healthfriend.domain.comment.accesscontrol;

import com.hf.healthfriend.auth.accesscontrol.AccessControlTrigger;
import com.hf.healthfriend.auth.accesscontrol.AccessController;
import com.hf.healthfriend.domain.comment.entity.Comment;
import com.hf.healthfriend.domain.comment.repository.CommentRepository;
import com.hf.healthfriend.domain.member.constant.Role;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthentication;

import java.util.Optional;

@Slf4j
@AccessController
@RequiredArgsConstructor
public class CommentAccessController {
    private final CommentRepository commentRepository;

    @AccessControlTrigger(path = "/hr/comments/{commentId}", method = "DELETE")
    public boolean accessControlForCreatingComment(BearerTokenAuthentication authentication, HttpServletRequest request) {
        return controlAccessToCommentResourceByCommentId(authentication, request);
    }

    @AccessControlTrigger(path = "/hr/comments/{commentId}", method = "PATCH")
    public boolean accessControlForUpdatingComment(BearerTokenAuthentication authentication, HttpServletRequest request) {
        return controlAccessToCommentResourceByCommentId(authentication, request);
    }

    private boolean controlAccessToCommentResourceByCommentId(BearerTokenAuthentication authentication, HttpServletRequest request) {
        // TODO: AccessControlFilter에서 글로벌하게 처리해야 한다 (혹은 AOP)
        if (authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).anyMatch((g) -> Role.ROLE_ADMIN.name().equals(g))) {
            return true;
        }

        String path = request.getRequestURI();
        long commentIdBeingAccessed = Long.parseLong(path.substring(path.lastIndexOf('/') + 1));

        Optional<Comment> commentOp = this.commentRepository.findById(commentIdBeingAccessed);
        if (commentOp.isEmpty()) {
            return true; // 그대로 진행시켜서 404 Not Found가 나도록
        }

        if (log.isTraceEnabled()) {
            log.trace("authentication={}", authentication);
            log.trace("authentication.principal={}", authentication.getPrincipal());
            log.trace("authentication.name={}", authentication.getName());
        }

        Comment comment = commentOp.get();
        return comment.getWriter().getId().equals(Long.parseLong(authentication.getName()));
    }
}
