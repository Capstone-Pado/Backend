package com.pado.backend.global.exception;

import org.springframework.http.HttpStatus;

public class ComponentDeletionNotAllowedException extends CustomException {
    public ComponentDeletionNotAllowedException(){
        super("RUNNING 상태의 컴포넌트는 삭제할 수 없습니다.", HttpStatus.BAD_REQUEST);
    }
}
