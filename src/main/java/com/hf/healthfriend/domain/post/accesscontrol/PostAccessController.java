package com.hf.healthfriend.domain.post.accesscontrol;

import com.hf.healthfriend.auth.accesscontrol.AccessControlTrigger;
import com.hf.healthfriend.auth.accesscontrol.AccessController;
import com.hf.healthfriend.domain.post.exception.CustomException;
import com.hf.healthfriend.domain.post.exception.PostErrorCode;
import com.hf.healthfriend.domain.post.repository.PostRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthentication;

@AccessController
@RequiredArgsConstructor
public class PostAccessController {

    private final PostRepository postRepository;

    @AccessControlTrigger(path = "/posts/{postId}",method = "PATCH")
    public boolean canUpdatePost(BearerTokenAuthentication authentication, HttpServletRequest request) {
        PostErrorCode errorCode = PostErrorCode.FORBIDDEN_UPDATE;
        return checkPostAccess(authentication,request,errorCode);
    }

    @AccessControlTrigger(path = "/posts/{postId}", method = "DELETE")
    public boolean canDeletePost(BearerTokenAuthentication authentication, HttpServletRequest request) {
        PostErrorCode errorCode = PostErrorCode.FORBIDDEN_DELETE;
        return checkPostAccess(authentication,request,errorCode);
    }

    private boolean checkPostAccess(BearerTokenAuthentication authentication, HttpServletRequest request,PostErrorCode errorCode) {
        String memberId = authentication.getName();
        String path = request.getRequestURI();
        Long postId = Long.parseLong(path.substring(path.lastIndexOf('/') + 1));

        return postRepository.findByPostIdAndIsDeletedFalse(postId)
                .map(post -> {
                    if(!post.getMember().getId().equals(memberId)){
                        throw new CustomException(errorCode);
                    }
                    return true;
                })
                .orElseThrow(() -> new CustomException(PostErrorCode.NON_EXIST_POST));
    }

}
