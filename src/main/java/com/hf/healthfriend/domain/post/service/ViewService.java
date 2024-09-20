package com.hf.healthfriend.domain.post.service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class ViewService {
    public boolean canAddViewCount(HttpServletRequest request, Long postId) {
        Cookie[] cookies = request.getCookies();
        if(cookies == null) {
            return true;
        }
        return Arrays.stream(cookies)
                .noneMatch(cookie -> cookie.getName().equals("visit") &&
                        cookie.getValue().contains(toString(postId)));
    }

    public Cookie createCookie(Long id) {
        Cookie cookie = new Cookie("visit", toString(id));
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60 * 24);
        return cookie;
    }

    private String toString(Long id) {
        return String.valueOf(id);
    }

}
