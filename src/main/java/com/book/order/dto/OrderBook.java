package com.book.order.dto;

import com.book.book.domain.BookId;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderBook {

    private BookId id;
    private int quantity;

}
