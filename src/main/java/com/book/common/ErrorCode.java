package com.book.common;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ErrorCode {

    VALIDATION_FAILED(400, "V001", "Validation is failed"),
    ENTITY_NOT_FOUND(400, "C002", " Entity Not Found"),


    MEMBER_DUPLICATION(400, "M001", "Member is duplicated"),
    MEMBER_NOT_FOUND(400, "M002", "Member was not found"),

    ORDER_NOT_FOUND(400, "O001", "Order was not found"),

    BOOK_NOT_FOUND(400, "B001", "Book was not found"),
    INVALID_INPUT_VALUE(400, "I001", " Invalid Input Value"),
    INVALID_TYPE_VALUE(400, "I002", " Invalid Type Value"),

    INTERNAL_SERVER_ERROR(500, "S001", "Server Error"),
    ;

    private final String code;
    private final String message;
    private int status;

    ErrorCode(final int status, final String code, final String message) {
        this.status = status;
        this.message = message;
        this.code = code;
    }

    public String message() {
        return this.message;
    }
    public String code() {
        return code;
    }
}
