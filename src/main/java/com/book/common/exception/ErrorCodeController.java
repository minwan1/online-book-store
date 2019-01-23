package com.book.common.exception;

import com.book.common.ErrorCode;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.EnumSet;

@RestController
@RequestMapping("error-codes")
public class ErrorCodeController {

    @GetMapping
    public EnumSet<ErrorCode> getErrorCodes() {
        return EnumSet.allOf(ErrorCode.class);
    }

}
