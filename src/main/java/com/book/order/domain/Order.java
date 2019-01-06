package com.book.order.domain;

import com.book.member.domain.Member;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.util.Assert;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "purchase_order")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order {

    @EmbeddedId
    private OrderNumber orderNumber;

    @Column(name = "total_amount")
    private Money totalAmount;

    @Embedded
    private Recipient recipient;

    @ManyToOne
    private Member orderer;

    @ElementCollection
    @CollectionTable(name ="order_line", joinColumns = @JoinColumn(name = "order_number"))
    @OrderColumn(name = "line_idx")
    private List<OrderLine> orderLines = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "order_number")
    private List<OrderStatusHistory> orderStatusHistories = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public static Order of(OrderNumber orderNumber, Member orderer, List<OrderLine> orderLines, Recipient recipient) {
        return new Order(orderNumber, orderer, orderLines, recipient);
    }

    public void addOrderStatus(OrderStatusHistory orderStatusHistory){
        orderStatusHistories.add(orderStatusHistory);
    }

    //for test
    @Builder
    public Order(OrderNumber orderNumber, Money totalAmount, Recipient recipient, Member orderer, List<OrderLine> orderLines, List<OrderStatusHistory> orderStatusHistories, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.orderNumber = orderNumber;
        this.totalAmount = totalAmount;
        this.recipient = recipient;
        this.orderer = orderer;
        this.orderLines = orderLines;
        this.orderStatusHistories = orderStatusHistories;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    private Order(OrderNumber orderNumber, Member orderer, List<OrderLine> orderLines, Recipient recipient) {

        Assert.notNull(orderNumber, "orderNumber must not be null!");
        Assert.notNull(orderLines, "member must not be null!");
        Assert.notNull(recipient, "recipient must not be null!");

        this.orderNumber = orderNumber;
        this.orderLines = orderLines;
        this.recipient = recipient;
        this.orderer = orderer;
        this.totalAmount = getOrderLineTotal(orderLines);
        addOrderStatus(OrderStatusHistory.of(OrderStatus.PREPARING));
    }

    private Money getOrderLineTotal(List<OrderLine> orderLines) {
        final int total = orderLines.stream()
                .mapToInt(orderLine -> orderLine.getAmount().getValue())
                .sum();
        return Money.of(total);
    }
}
