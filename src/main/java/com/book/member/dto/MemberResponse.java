package com.book.member.dto;

import com.book.member.domain.Email;
import com.book.member.domain.Member;
import com.book.member.domain.Name;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

//import com.book.member.domain.Name;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberResponse {

    private long id;
    private Email email;
    private Name name;

    public MemberResponse(Member member) {
        this.id = member.getId();
        this.email = member.getEmail();
        this.name = member.getName();
    }

    public static MemberResponse of(Member member){
        return new MemberResponse(member);
    }

    public static List<MemberResponse> of(List<Member> members) {
        return members.stream()
                .map(member -> MemberResponse.of(member))
                .collect(Collectors.toList());

    }
}
