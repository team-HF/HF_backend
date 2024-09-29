package com.hf.healthfriend.domain.spec.controller;

import com.hf.healthfriend.domain.spec.dto.SpecDto;
import com.hf.healthfriend.domain.spec.service.SpecService;
import com.hf.healthfriend.global.spec.ApiBasicResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/hr")
@RequiredArgsConstructor
public class SpecController {
    private final SpecService specService;

    @PostMapping("/members/{memberId}/specs")
    public ResponseEntity<ApiBasicResponse<Long>> addSpecToMember(@PathVariable("memberId") Long memberId,
                                                                  @RequestBody SpecDto dto) {
        Long generatedId = this.specService.addSpec(memberId, dto);
        return new ResponseEntity<>(
                ApiBasicResponse.of(generatedId, HttpStatus.CREATED),
                HttpStatus.CREATED
        );
    }
}
