package com.pado.backend.global.exception;

import org.springframework.http.HttpStatus;

public class ProjectDeletionNotAllowedException extends CustomException {
    public ProjectDeletionNotAllowedException() {
        super("RUNNING 상태에서는 프로젝트를 삭제할 수 없습니다.", HttpStatus.CONFLICT);
    }
}