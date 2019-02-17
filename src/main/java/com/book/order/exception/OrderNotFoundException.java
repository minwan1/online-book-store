package com.book.order.exception;

import com.book.common.ErrorCode;

public class OrderNotFoundException extends RuntimeException{

    public OrderNotFoundException(){
        super(ErrorCode.ORDER_NOT_FOUND.message());
    }

}
