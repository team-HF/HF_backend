package com.hf.healthfriend.domain.like.controller;

import com.hf.healthfriend.domain.like.service.LikeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class LikeController {
    private final LikeService likeService;
}
