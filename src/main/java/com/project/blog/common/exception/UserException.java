package com.project.blog.common.exception;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.io.Serial;

@Getter
@Setter
@NoArgsConstructor
public class UserException extends RuntimeException{

    private HttpStatus status;
    private String message;

    public UserException(HttpStatus status, String message) {
        super(message);
        this.status = status;
        this.message = message;
    }
}