package com.book.member.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Name {

    @NotEmpty
    @Column(name = "first_name", nullable = false)
    private String firstname;

    @NotEmpty
    @Column(name = "last_name", nullable = false)
    private String lastname;

    public String getFullName(){
        return this.firstname +" "+this.lastname;
    }


}
