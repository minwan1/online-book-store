package com.book.member.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
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
public class Name {

    @NotEmpty
    @Column(name = "first_name", nullable = false)
    private String firstname;

    @NotEmpty
    @Column(name = "last_name", nullable = false)
    private String lastname;

    public Name(final String firstname,final String lastname) {
        this.firstname = firstname;
        this.lastname = lastname;
    }


    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    public String getFullName(){
        return this.firstname +" "+this.lastname;
    }


}
