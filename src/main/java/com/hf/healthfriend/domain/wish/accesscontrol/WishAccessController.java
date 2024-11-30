package com.hf.healthfriend.domain.wish.accesscontrol;

import com.hf.healthfriend.auth.accesscontrol.AccessControlTrigger;
import com.hf.healthfriend.auth.accesscontrol.AccessController;
import com.hf.healthfriend.domain.wish.exception.WishErrorCode;
import com.hf.healthfriend.domain.wish.exception.WishException;
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
        WishErrorCode errorCode = WishErrorCode.FORBIDDEN_WISH_DELETE;
        return checkWishAccess(authentication,request,errorCode);
    }

    private boolean checkWishAccess(BearerTokenAuthentication authentication, HttpServletRequest request,WishErrorCode errorCode) {
        String memberId = authentication.getName();
        String path = request.getRequestURI();
        Long wishId = Long.parseLong(path.substring(path.lastIndexOf('/') + 1));

        return wishRepository.findByWishIdAndIsDeletedFalse(wishId)
                .map(wish -> {
                    if(!wish.getWisher().getName().equals(memberId)){
                        throw new WishException(errorCode);
                    }
                    return true;
                })
                .orElseThrow(() -> new WishException(WishErrorCode.WISH_NOT_FOUND));
    }

}
