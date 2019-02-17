package com.book.order.dto;

import com.book.book.domain.BookId;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderBook {

    private BookId id;
    private int quantity;

    @Builder //for test
    public OrderBook(BookId id, int quantity) {
        this.id = id;
        this.quantity = quantity;
    }
}
