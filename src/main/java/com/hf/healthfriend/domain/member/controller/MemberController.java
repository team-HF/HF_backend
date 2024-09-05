package com.hf.healthfriend.domain.member.controller;

import com.hf.healthfriend.domain.member.dto.request.MemberCreationRequestDto;
import com.hf.healthfriend.domain.member.dto.response.MemberCreationResponseDto;
import com.hf.healthfriend.domain.member.service.MemberService;
import com.hf.healthfriend.global.spec.ApiBasicResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.net.URISyntaxException;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;

    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<ApiBasicResponse<MemberCreationResponseDto>> createMember(@RequestBody MemberCreationRequestDto requestBody) throws URISyntaxException {
        log.info("Request Body:\n{}", requestBody);
        MemberCreationResponseDto result = this.memberService.createMember(requestBody);
        return ResponseEntity.created(new URI("/members/" + result.getMemberId()))
                .body(ApiBasicResponse.of(result, HttpStatus.CREATED));
    }
}
