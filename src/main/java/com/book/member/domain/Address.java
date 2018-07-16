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
public class Address {

    @NotEmpty
    @Column(name = "city", nullable = false)
    private String city;

    @NotEmpty
    @Column(name = "street" , nullable = false)
    private String street;

    @NotEmpty
    @Column(name = "address1" , nullable = false)
    private String zipcode;

}
