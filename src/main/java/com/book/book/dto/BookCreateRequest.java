package com.book.book.dto;

import com.book.order.domain.Money;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BookCreateRequest {

    private String name;
    private Money price;

}
