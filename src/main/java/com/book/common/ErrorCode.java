package com.book.common;

public enum ErrorCode {

    VALIDATION_FAILED("V001", "Validation is failed"),


    MEMBER_DUPLICATION("M001", "Member is duplicated"),
    MEMBER_NOT_FOUND("M002", "Member was not found"),

    ORDER_NOT_FOUND("O001", "Order was not found"),

    BOOK_NOT_FOUND("B001", "Book was not found")
    ;

    private final String code;
    private final String message;

    ErrorCode( String code, String message) {
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
