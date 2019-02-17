package com.book.member.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotEmpty;

@Getter
@Embeddable
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Name {

    @NotEmpty
    @Column(name = "first_name", nullable = false)
    private String firstName;

    @NotEmpty
    @Column(name = "last_name", nullable = false)
    private String lastName;

    public Name(final String firstName, final String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public static Name of(final String firstName, final String lastName){
        return new Name(firstName, lastName);
    }

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    public String getFullName(){
        return this.firstName +" "+this.lastName;
    }

}
