package com.mysite.sbb.global.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// 찾는 데이터가 없을 때 던지는 예외.
// @ResponseStatus: 이 예외가 컨트롤러 밖으로 나가면 스프링이 500 대신 404 Not Found 로 응답한다.
// RuntimeException: 메서드 선언에 throws 를 적지 않아도 된다(unchecked).
@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "entity not found")
public class DataNotFoundException extends RuntimeException {
    public DataNotFoundException(String message) {
        super(message);
    }
}
