package com.mysite.sbb.global.exception;

import lombok.Getter;

// 서비스의 업무 규칙 위반(예: 이미 있는 아이디). 어느 입력칸의 문제인지 field 로 알려 준다.
// 컨트롤러가 받아서 폼 오류로 바꿔 보여 준다.
@Getter
public class ServiceException extends RuntimeException {
    private final String field;

    public ServiceException(String field, String message) {
        super(message);
        this.field = field;
    }
}
