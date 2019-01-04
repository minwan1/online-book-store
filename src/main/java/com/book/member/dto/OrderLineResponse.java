package com.book.member.dto;

import com.book.book.dto.BookResponse;
import com.book.order.domain.Money;
import com.book.order.domain.OrderLine;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class OrderLineResponse {

    private BookResponse bookResponse;
    private int quantity;
    private Money amount;

    public OrderLineResponse(OrderLine orderLine) {
        this.bookResponse = BookResponse.of(orderLine.getBook());
        this.quantity = orderLine.getQuantity();
        this.amount = orderLine.getAmount();
    }

    private static OrderLineResponse of(OrderLine orderLine) {
        return new OrderLineResponse(orderLine);
    }

    public static List<OrderLineResponse> of(List<OrderLine> orderLines) {
        return orderLines.stream()
                .map(orderLine -> OrderLineResponse.of(orderLine))
                .collect(Collectors.toList());
    }

}
