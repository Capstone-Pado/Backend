package com.pado.backend.global.exception;

import org.springframework.http.HttpStatus;

public class UnauthorizedComponentAccessException extends CustomException {
    public UnauthorizedComponentAccessException() {
        super("해당 프로젝트에 속한 컴포넌트가 아닙니다.", HttpStatus.FORBIDDEN);
    }
}