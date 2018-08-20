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
    private final MemberHelperService memberHelperService;

    public Member signUp(final MemberSignupRequest request){
        memberHelperService.verifyEmailIsDuplicated(request.getEmail());
        final Member member = memberRepository.save(request.toMember());
        return member;
    }

}