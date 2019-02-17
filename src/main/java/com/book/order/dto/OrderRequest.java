package com.book.order.dto;

import com.book.order.domain.Recipient;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderRequest {

    private List<OrderBook> orderBooks = new ArrayList<>();

    private long memberId;

    @Valid
    private Recipient recipient;

    @Builder //for test
    public OrderRequest(List<OrderBook> orderBooks, Long memberId, @Valid Recipient recipient) {
        this.orderBooks = orderBooks;
        this.memberId = memberId;
        this.recipient = recipient;
    }
}
