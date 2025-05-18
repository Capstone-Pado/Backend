package com.pado.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.pado.backend.dto.request.UserLoginRequestDto;
import com.pado.backend.dto.request.UserLogoutRequestDto;
import com.pado.backend.dto.request.UserRegisterRequestDto;
import com.pado.backend.dto.response.DefaultResponseDto;
import com.pado.backend.dto.response.UserLoginResponseDto;
import com.pado.backend.dto.response.UserRegisterResponseDto;
import com.pado.backend.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Tag(name = "Auth", description = "인증 관련 API")
public class AuthController {
    private final AuthService authService;

    @Operation(summary = "회원가입", description = "유저 정보를 기반으로 회원가입을 진행합니다.")
    @PostMapping("/signup")
    public ResponseEntity<UserRegisterResponseDto> signup(@RequestBody UserRegisterRequestDto request) {
        return ResponseEntity.ok(authService.signup(request));
    }

    @Operation(summary = "로그인", description = "이메일과 비밀번호로 로그인을 시도합니다.")
    @PostMapping("/signin")
    public ResponseEntity<UserLoginResponseDto> signin(@RequestBody UserLoginRequestDto request) {
        return ResponseEntity.ok(authService.signin(request));
    }

    @Operation(summary = "로그아웃", description = "로그아웃 처리를 수행합니다.")
    @PostMapping("/signout")
    public ResponseEntity<DefaultResponseDto> signout(@RequestBody UserLogoutRequestDto request) {
        return ResponseEntity.ok(authService.signout(request));
    }
}
