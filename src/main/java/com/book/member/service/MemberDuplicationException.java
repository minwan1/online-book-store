package com.book.member.service;

import com.book.common.ErrorCode;

public class MemberDuplicationException extends RuntimeException {
    public MemberDuplicationException(){
        super(ErrorCode.MEMBER_DUPLICATION.message());
    }
}
