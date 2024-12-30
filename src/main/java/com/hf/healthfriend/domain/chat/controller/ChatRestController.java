package com.hf.healthfriend.domain.chat.controller;

import com.hf.healthfriend.domain.chat.constant.ChatroomListSearchCondition;
import com.hf.healthfriend.domain.chat.dto.response.ChatroomListResponseDto;
import com.hf.healthfriend.domain.chat.service.ChatService;
import com.hf.healthfriend.global.spec.ApiBasicResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/hf")
@RequiredArgsConstructor
public class ChatRestController {
    private final ChatService chatService;

    @GetMapping("/members/{participantId}/chatrooms")
    public ResponseEntity<ApiBasicResponse<List<ChatroomListResponseDto>>> getChatroomList(
            @PathVariable("participantId") Long participantId,
            @RequestParam("searchCondition") ChatroomListSearchCondition searchCondition,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", required = false) Integer pageSize
    ) {
        return ResponseEntity.ok(
                ApiBasicResponse.of(this.chatService.getChatroomList(participantId, searchCondition, page, pageSize), HttpStatus.OK)
        );
    }
}
