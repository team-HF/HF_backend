package com.hf.healthfriend.domain.member.controller;

import com.hf.healthfriend.auth.itself.dto.request.SignUpRequestDto;
import com.hf.healthfriend.domain.member.service.MemberService;
import com.hf.healthfriend.global.spec.ApiBasicResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member")
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/check-duplicate")
    public ResponseEntity<ApiBasicResponse> checkDuplicate(
            @RequestParam String email
    ){
        ApiBasicResponse response = memberService.checkDuplicateEmail(email);

        return ResponseEntity.status(response.getCode()).body(response);
    }

    @PostMapping("/sign-up")
    public ResponseEntity<ApiBasicResponse> signUpMember(
            @RequestBody @Valid SignUpRequestDto signUpRequestDto
    ){
        ApiBasicResponse response = memberService.signUp(signUpRequestDto);

        return ResponseEntity.status(response.getCode()).body(response);
    }


}
