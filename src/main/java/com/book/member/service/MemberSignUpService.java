package com.book.member.service;

import com.book.member.domain.Member;
import com.book.member.dto.MemberSignupRequest;
import com.book.member.repository.MemberRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class MemberSignUpService {

    private final MemberRepository memberRepository;
    private final MemberFindService memberFindService;

    public Member signUp(final MemberSignupRequest request){
        memberFindService.verifyEmailIsDuplicated(request.getEmail());
        Member member = request.toMember();
        memberRepository.save(member);
        return member;
    }

}