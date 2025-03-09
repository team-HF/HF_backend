package com.hf.healthfriend.domain.wish.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class WishRequestDto {
    @NotNull
    private Long wisherId;
    @NotNull
    private Long wishedId;
}
