package com.hf.healthfriend.domain.comment.accesscontrol;

import com.hf.healthfriend.auth.accesscontrol.AccessControlTrigger;
import com.hf.healthfriend.auth.accesscontrol.AccessController;
import com.hf.healthfriend.domain.comment.entity.Comment;
import com.hf.healthfriend.domain.comment.exception.CommentErrorCode;
import com.hf.healthfriend.domain.comment.exception.CommentException;
import com.hf.healthfriend.domain.comment.repository.CommentJpaRepository;
import com.hf.healthfriend.domain.comment.repository.CommentRepository;
import com.hf.healthfriend.domain.member.constant.Role;
import com.hf.healthfriend.domain.post.exception.PostErrorCode;
import com.hf.healthfriend.domain.post.exception.PostException;
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
    private final CommentJpaRepository commentJpaRepository;

    @AccessControlTrigger(path = "/hf/comments/{commentId}", method = "DELETE")
    public boolean accessControlForCreatingComment(BearerTokenAuthentication authentication, HttpServletRequest request) {
        CommentErrorCode errorCode = CommentErrorCode.FORBIDDEN_COMMENT_DELETE;
        return controlAccessToCommentResourceByCommentId(authentication, request,errorCode);
    }

    @AccessControlTrigger(path = "/hf/comments/{commentId}", method = "PATCH")
    public boolean accessControlForUpdatingComment(BearerTokenAuthentication authentication, HttpServletRequest request) {
        CommentErrorCode errorCode = CommentErrorCode.FORBIDDEN_COMMENT_UPDATE;
        return controlAccessToCommentResourceByCommentId(authentication, request,errorCode);
    }

    private boolean controlAccessToCommentResourceByCommentId(BearerTokenAuthentication authentication, HttpServletRequest request, CommentErrorCode errorCode) {
        String memberName = authentication.getName();
        String path = request.getRequestURI();
        Long commentId = Long.parseLong(path.substring(path.lastIndexOf('/') + 1));

        return commentJpaRepository.findByCommentIdAndIsDeletedFalse(commentId)
                .map(comment -> {
                    if (!comment.getWriter().getId().equals(Long.valueOf(memberName))) {
                        throw new CommentException(errorCode);
                    }
                    return true;
                })
                .orElseThrow(() -> new CommentException(CommentErrorCode.COMMENT_NOT_FOUND));
    }
}
