package com.hf.healthfriend.domain.spec.controller;

import com.hf.healthfriend.domain.spec.dto.SpecDto;
import com.hf.healthfriend.domain.spec.dto.request.SpecUpdateRequestDto;
import com.hf.healthfriend.domain.spec.dto.response.SpecUpdateResponseDto;
import com.hf.healthfriend.domain.spec.service.SpecService;
import com.hf.healthfriend.global.spec.ApiBasicResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/hf")
public class SpecController {
    private final SpecService specService;

    @PostMapping("/members/{memberId}/specs")
    public ResponseEntity<ApiBasicResponse<List<Long>>> addSpecs(@PathVariable("memberId") Long memberId,
                                                                 @RequestBody @Valid List<SpecDto> dtoList) {
        List<Long> result = this.specService.addSpec(memberId, dtoList);
        return new ResponseEntity<>(
                ApiBasicResponse.of(result, HttpStatus.CREATED),
                HttpStatus.CREATED
        );
    }

    @GetMapping("/members/{memberId}/specs")
    public ResponseEntity<ApiBasicResponse<List<SpecDto>>> getSpecsOfSpecificMember(@PathVariable("memberId") Long memberId) {
        List<SpecDto> result = this.specService.getSpecsOfMember(memberId);
        return ResponseEntity.ok(
                ApiBasicResponse.of(result, HttpStatus.OK)
        );
    }

    @PutMapping("/members/{memberId}/specs")
    public ResponseEntity<ApiBasicResponse<SpecUpdateResponseDto>> updateSpecs(@PathVariable("memberId") Long memberId,
                                                                               @RequestBody @Valid List<SpecUpdateRequestDto> dto) {
        SpecUpdateResponseDto result = this.specService.updateSpecsOfMember(memberId, dto);
        return ResponseEntity.ok(
                ApiBasicResponse.of(result, HttpStatus.OK)
        );
    }
}
