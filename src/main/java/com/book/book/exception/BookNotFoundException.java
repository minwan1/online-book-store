package com.book.book.exception;

import com.book.common.ErrorCode;

public class BookNotFoundException extends RuntimeException{
    public BookNotFoundException() {
        super(ErrorCode.BOOK_NOT_FOUND.message());
    }
}
