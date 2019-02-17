package com.book.order.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.bytebuddy.utility.RandomString;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderNumber implements Serializable {

    @Column(name="order_number")
    private String number;

    private OrderNumber(String number) {
        this.number = number;
    }

    public static OrderNumber of(String value) {
        return new OrderNumber(value);
    }

    public static OrderNumber generateOrderNumber(){
        return new OrderNumber(RandomString.make(8));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderNumber orderNo = (OrderNumber) o;
        return Objects.equals(number, orderNo.number);
    }

    @Override
    public int hashCode() {
        return Objects.hash(number);
    }

}