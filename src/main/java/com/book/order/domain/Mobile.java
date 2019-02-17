package com.book.order.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotEmpty;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Mobile {

    @NotEmpty
    @Column(name = "mobile", nullable = false)
    private String value;

    public Mobile(String value) {
        this.value = value;
    }

    public static Mobile of(String mobile){
        return new Mobile(mobile);
    }

}
