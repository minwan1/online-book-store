package com.book.common.jpa;


import com.book.order.domain.Money;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class MoneyConverter implements AttributeConverter<Money, Integer> {

    @Override
    public Integer convertToDatabaseColumn(Money money) {
        if (money == null)
            return null;
        else
            return money.getValue();
    }

    @Override
    public Money convertToEntityAttribute(Integer value) {
        if (value == null) return null;
        else return Money.of(value);
    }
}
