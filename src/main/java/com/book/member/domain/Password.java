package com.book.member.domain;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;

@Getter
@Embeddable
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Password {

    @NotEmpty
    @Column(name = "password", nullable = false)
    private String value;

    public Password(String value) {
        this.value = value;
    }
}
