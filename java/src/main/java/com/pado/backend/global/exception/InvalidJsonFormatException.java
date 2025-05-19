package com.pado.backend.global.exception;

import org.springframework.http.HttpStatus;

public class InvalidJsonFormatException extends CustomException {
     public InvalidJsonFormatException() {
        super("설정 JSON 형식이 올바르지 않습니다.", HttpStatus.BAD_REQUEST);
    }
}
