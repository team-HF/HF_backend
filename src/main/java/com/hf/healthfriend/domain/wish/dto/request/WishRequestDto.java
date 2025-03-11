package com.hf.healthfriend.domain.wish.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WishRequestDto {
    @NotNull
    private Long wisherId;
    @NotNull
    private Long wishedId;
}
