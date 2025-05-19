package com.pado.backend.global.exception;

import org.springframework.http.HttpStatus;

public class InvalidComponentRequestException extends CustomException {
    public InvalidComponentRequestException() {
        super("SERVICE 타입은 반드시 parentComponentId가 필요합니다.", HttpStatus.BAD_REQUEST);
    }
}
