package com.pado.backend.global.exception;

import org.springframework.http.HttpStatus;

public class ComponentProjectMismatchException extends CustomException {
    public ComponentProjectMismatchException() {
        super("해당 컴포넌트는 요청한 프로젝트에 속해 있지 않습니다.", HttpStatus.BAD_REQUEST);
    }
}