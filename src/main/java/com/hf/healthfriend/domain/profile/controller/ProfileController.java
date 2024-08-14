package com.hf.healthfriend.domain.profile.controller;

import com.hf.healthfriend.auth.principal.PrincipalDetails;
import com.hf.healthfriend.domain.profile.dto.request.FitnessProfileDto;
import com.hf.healthfriend.domain.profile.dto.request.NicknameDto;
import com.hf.healthfriend.domain.profile.service.ProfileService;
import com.hf.healthfriend.global.spec.ApiBasicResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/profile")
public class ProfileController {

    private final ProfileService profileService;

    @PostMapping("/nickname")
    public ResponseEntity<ApiBasicResponse> setNickname(
            @RequestBody NicknameDto nicknameDto,
            @AuthenticationPrincipal PrincipalDetails principalDetails) {

        ApiBasicResponse apiBasicResponse = profileService.setNickname(nicknameDto, principalDetails);

        return ResponseEntity.status(apiBasicResponse.getCode()).body(apiBasicResponse);
    }

    @PostMapping("/fitness-profile")
    public ResponseEntity<ApiBasicResponse> initFitnessProfile(
            @RequestBody FitnessProfileDto fitnessProfileDto,
            @AuthenticationPrincipal PrincipalDetails principalDetails){

        ApiBasicResponse apiBasicResponse = profileService.setProfileLevel(fitnessProfileDto, principalDetails);

        return ResponseEntity.status(apiBasicResponse.getCode()).body(apiBasicResponse);
    }
}
