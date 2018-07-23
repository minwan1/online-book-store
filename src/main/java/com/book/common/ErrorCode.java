package com.book.common;

public enum ErrorCode {

    VALIDATION_FAILED("V001", "Validation is failed"),
    MEMBER_DUPLICATION("M001", "Member is duplicated"),
    MEMBER_NOT_FOUND("M002", "Member is not found")
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
