package com.book.member.dto;

import com.book.member.domain.Email;
import com.book.member.domain.Member;
import com.book.member.domain.Name;
import com.book.member.domain.Password;
import lombok.*;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;


@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberSignupRequest {

    @Valid
    private Email email;
    @Valid
    private Name name;
    @Valid
    private Password password;

    @Builder
    public MemberSignupRequest(Email email, Name name, Password password) {
        this.email = email;
        this.name = name;
        this.password = password;
    }

    public Member toMember(){
        return new Member(email,password, name);
    }
}
