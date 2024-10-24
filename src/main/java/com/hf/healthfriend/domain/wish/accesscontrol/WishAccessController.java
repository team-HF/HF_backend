package com.hf.healthfriend.domain.wish.accesscontrol;

import com.hf.healthfriend.auth.accesscontrol.AccessControlTrigger;
import com.hf.healthfriend.auth.accesscontrol.AccessController;
import com.hf.healthfriend.domain.wish.repository.WishRepository;
import com.hf.healthfriend.global.exception.CustomException;
import com.hf.healthfriend.global.exception.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthentication;

@AccessController
@RequiredArgsConstructor
public class WishAccessController {

    private final WishRepository wishRepository;

    @AccessControlTrigger(path = "hf/wish/{wishId}",method = "DELETE")
    public boolean canDeleteWish(BearerTokenAuthentication authentication, HttpServletRequest request) {
        ErrorCode errorCode = ErrorCode.FORBIDDEN_WISH_DELETE;
        return checkWishAccess(authentication,request,errorCode);
    }

    private boolean checkWishAccess(BearerTokenAuthentication authentication, HttpServletRequest request,ErrorCode errorCode) {
        String memberId = authentication.getName();
        String path = request.getRequestURI();
        Long wishId = Long.parseLong(path.substring(path.lastIndexOf('/') + 1));

        return wishRepository.findByWishIdAndIsDeletedFalse(wishId)
                .map(wish -> {
                    if(!wish.getWisher().getName().equals(memberId)){
                        throw new com.hf.healthfriend.global.exception.CustomException(errorCode);
                    }
                    return true;
                })
                .orElseThrow(() -> new CustomException(ErrorCode.NON_EXIST_WISH));
    }

}
