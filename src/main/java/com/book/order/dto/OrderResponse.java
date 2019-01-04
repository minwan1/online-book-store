package com.book.order.dto;

import com.book.member.dto.OrderLineResponse;
import com.book.order.domain.Money;
import com.book.order.domain.Order;
import com.book.order.domain.OrderNumber;
import com.book.order.domain.Recipient;
import lombok.Getter;

import java.util.List;


@Getter
public class OrderResponse {

    private OrderNumber orderNumber;
    private Money totalAmount;
    private Recipient recipient;
    private List<OrderLineResponse> orderLineResponse;
    private List<OrderStatusHistoryResponse> orderStatusHistoryResponse;

    private OrderResponse(Order order) {
        this.orderNumber = order.getOrderNumber();
        this.totalAmount = order.getTotalAmount();
        this.recipient = order.getRecipient();
        this.orderLineResponse = OrderLineResponse.of(order.getOrderLines());
        this.orderStatusHistoryResponse = OrderStatusHistoryResponse.of(order.getOrderStatusHistories());
    }

    public static OrderResponse of(Order order) {
        return new OrderResponse(order);
    }
}
