package com.book.order.domain;


import com.book.book.domain.Book;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderLine {

    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false, updatable = false)
    private Book book;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "amount", nullable = false)
    private Money amount;

    public OrderLine(final Book book, final int quantity) {
        this.book = book;
        this.quantity = quantity;
        this.amount = book.calculate(quantity);
    }

    public static OrderLine of(final Book book, final int quantity) {
        return new OrderLine(book, quantity);
    }
}
