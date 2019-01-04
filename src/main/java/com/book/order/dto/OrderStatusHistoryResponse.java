package com.book.order.dto;

import com.book.order.domain.OrderStatus;
import com.book.order.domain.OrderStatusHistory;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


@Getter
public class OrderStatusHistoryResponse {

    private OrderStatus orderStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public OrderStatusHistoryResponse(OrderStatusHistory orderStatusHistory) {
        this.orderStatus = orderStatusHistory.getOrderStatus();
        this.createdAt = orderStatusHistory.getCreatedAt();
        this.updatedAt = orderStatusHistory.getUpdatedAt();
    }

    public static List<OrderStatusHistoryResponse> of(List<OrderStatusHistory> orderStatusHistories) {
        return orderStatusHistories.stream()
                .map(orderStatusHistory -> OrderStatusHistoryResponse.of(orderStatusHistory))
                .collect(Collectors.toList());

    }

    private static OrderStatusHistoryResponse of(OrderStatusHistory orderStatusHistory) {
        return new OrderStatusHistoryResponse(orderStatusHistory);
    }
}
