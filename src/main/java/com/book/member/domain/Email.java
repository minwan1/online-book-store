package com.book.member.domain;

import com.book.config.payment.Payment;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@JsonIgnoreProperties({"host", "id"})
public class Email {

    //모든데서 다체크해야하는 이슈발생.
    @NotEmpty
    @org.hibernate.validator.constraints.Email
    @Column(name = "email", nullable = false, unique = true)
    private String value;

    public Email(String value) {
        this.value = value;
    }

    public String getHost() {
        int index = value.indexOf("@");
        return value.substring(index);
    }

    public String getId() {
        int index = value.indexOf("@");
        return value.substring(0, index);
    }
}