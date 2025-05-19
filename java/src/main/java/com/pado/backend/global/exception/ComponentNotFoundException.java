package com.pado.backend.global.exception;

import org.springframework.http.HttpStatus;

public class ComponentNotFoundException extends CustomException {
    public ComponentNotFoundException() {
        super("해당 컴포넌트를 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
    }
}