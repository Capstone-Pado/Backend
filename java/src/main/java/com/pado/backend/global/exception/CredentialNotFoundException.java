package com.pado.backend.global.exception;

import org.springframework.http.HttpStatus;

public class CredentialNotFoundException extends CustomException {
    public CredentialNotFoundException() {
        super("해당 크레덴셜을 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
    }
}
