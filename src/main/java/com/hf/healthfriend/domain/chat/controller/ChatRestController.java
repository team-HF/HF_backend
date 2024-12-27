package com.hf.healthfriend.domain.chat.controller;

import com.hf.healthfriend.domain.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/hf/chat")
@RequiredArgsConstructor
public class ChatRestController {
    private final ChatService chatService;
}
