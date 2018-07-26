package com.book.member.service;

import com.book.member.domain.Email;
import com.book.member.domain.Member;
import com.book.member.repository.MemberRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class MemberFindService {

    private final MemberRepository memberRepository;

    public Member findById(final long id){
        final Member member = memberRepository.findOne(id);
        if(member == null) throw new MemberNotFoundException();
        return member;
    }

    public void verifyEmailIsDuplicated(final Email email){
        if(isEmailDuplicated(email)) throw new MemberDuplicationException();
    }
    private boolean isEmailDuplicated(final Email email){
        return memberRepository.findByEmail(email) != null;
    }


}
