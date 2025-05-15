package com.pado.backend.global.exception;

import org.springframework.http.HttpStatus;

public class UnauthorizedCredentialAccessException extends CustomException {
    public UnauthorizedCredentialAccessException() {
        super("해당 유저의 크레덴셜이 아닙니다.", HttpStatus.FORBIDDEN);
    }
}
