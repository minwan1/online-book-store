package com.book.order.domain;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Money {

    private int value;

    public Money(int value) {
        this.value = value;
    }

    public static Money of(Integer value) {
        return new Money(value);

    }
}
