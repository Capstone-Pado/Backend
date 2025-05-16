package com.pado.backend.global.exception;

import org.springframework.http.HttpStatus;

public class ProjectNotFoundException extends CustomException {
    public ProjectNotFoundException() {
        super("해당 프로젝트를 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
    }
}
