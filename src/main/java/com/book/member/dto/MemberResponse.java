package com.book.member.dto;

import com.book.member.domain.Email;
import com.book.member.domain.Member;
//import com.book.member.domain.Name;
import com.book.member.domain.Name;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberResponse {

    private Email email;
    private Name name;

    public MemberResponse(Member member) {
        this.email = member.getEmail();
        this.name = member.getName();
    }
}
