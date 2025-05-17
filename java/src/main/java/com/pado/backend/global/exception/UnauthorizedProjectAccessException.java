package com.pado.backend.global.exception;

import org.springframework.http.HttpStatus;

public class UnauthorizedProjectAccessException extends CustomException {
    public UnauthorizedProjectAccessException() {
        super("해당 유저의 프로젝트가 아닙니다.", HttpStatus.FORBIDDEN);
    }
}
