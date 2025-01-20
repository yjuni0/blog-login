package com.project.blog.common.exception;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.io.Serial;

@Getter
@NoArgsConstructor
public class UserException extends RuntimeException {
    private HttpStatus status;

    public UserException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    // 추가적으로 유용한 생성자나 메서드
    public UserException(String message) {
        super(message);
        this.status = HttpStatus.BAD_REQUEST; // 기본 상태 코드
    }

    public int getStatusCode() {
        return status.value();
    }
}