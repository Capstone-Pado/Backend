package com.pado.backend.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pado.backend.domain.User;
import com.pado.backend.dto.request.UserLoginRequestDto;
import com.pado.backend.dto.request.UserLogoutRequestDto;
import com.pado.backend.dto.request.UserRegisterRequestDto;
import com.pado.backend.dto.response.DefaultResponseDto;
import com.pado.backend.dto.response.UserLoginResponseDto;
import com.pado.backend.dto.response.UserRegisterResponseDto;
import com.pado.backend.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
// TODO: 서비스 로직 구현
public class AuthService {
    private final UserRepository userRepository;

    public UserRegisterResponseDto signup(UserRegisterRequestDto request) {
        return null;
    }

    public UserLoginResponseDto signin(UserLoginRequestDto request) {
        return null;
    }

    public DefaultResponseDto signout(UserLogoutRequestDto request) {
       return null;
    }
}
