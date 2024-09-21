package com.hf.healthfriend.domain.post.service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class ViewService {
    public boolean canAddViewCount(String cookieValue, Long postId) {
        if(cookieValue == null || cookieValue.isEmpty()) {
            return true;
        }
        return !cookieValue.contains(String.valueOf(postId));
    }

    public String addPostIdToVisitCookie(String visitCookieValue, Long postId) {
        if (visitCookieValue == null) {
            return "/" + postId + "/";
        }
        return visitCookieValue + postId + "/";
    }

}
