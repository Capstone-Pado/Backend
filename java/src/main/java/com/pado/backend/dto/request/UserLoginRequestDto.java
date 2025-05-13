package com.pado.backend.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginRequestDto {
    @Schema(description = "사용자 이름", example = "홍길동")
    private String userName;
    @Schema(description = "비밀번호", example = "pado123!")
    private String password;
}
