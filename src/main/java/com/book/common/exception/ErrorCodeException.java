package com.book.common.exception;

import com.book.common.ErrorCode;
import lombok.Getter;

@Getter
public class ErrorCodeException extends RuntimeException {

    private ErrorCode code;

    public ErrorCodeException(String message, ErrorCode code) {
        super(message);
        this.code = code;
    }
}
