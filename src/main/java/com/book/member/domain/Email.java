package com.book.member.domain;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Getter
@Embeddable
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Email {

    //모든데서 다체크해야하는 이슈발생.
    @NotEmpty
    @org.hibernate.validator.constraints.Email
    @Column(name = "email", nullable = false, unique = true)
    private String value;

    public Email(String value) {
        this.value = value;
    }

}