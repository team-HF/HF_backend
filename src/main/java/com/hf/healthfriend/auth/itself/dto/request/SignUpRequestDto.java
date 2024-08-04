package com.hf.healthfriend.auth.itself.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;

@Getter
public class SignUpRequestDto {

    @NotEmpty(message = "NickName은 null일 수 없습니다")
    private String nickName;

    @Email(message = "Email의 형식이 잘못되었습니다")
    private String email;

    @NotEmpty(message = "Password는 null일 수 없습니다")
    private String password;

    private double latitude;

    private double longitude;
}

