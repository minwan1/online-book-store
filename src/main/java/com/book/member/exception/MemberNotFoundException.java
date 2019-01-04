package com.book.member.exception;

import com.book.common.ErrorCode;

public class MemberNotFoundException extends RuntimeException{
    public MemberNotFoundException(){
        super(ErrorCode.MEMBER_NOT_FOUND.message());
    }
}
