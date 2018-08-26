package com.book.member.service;

import com.book.member.domain.Email;
import com.book.member.domain.Member;
import com.book.member.repository.MemberRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class MemberHelperServiceTest {

    @InjectMocks
    private MemberHelperService memberHelperService;
    @Mock
    private MemberRepository memberRepository;

    private final String TEST_EMAIL = "test@test.com";


    @Test(expected = MemberDuplicationException.class)
    public void verifyEmailIsDuplicated_EmailIsDuplicated_MemberDuplicationException() {

        //given
        final Member member = new Member(new Email(TEST_EMAIL), null, null);
        given(memberRepository.findByEmail(member.getEmail())).willReturn(member);

        //when
        memberHelperService.verifyEmailIsDuplicated(member.getEmail());

        //then

    }

    @Test
    public void verifyEmailIsDuplicated_EmailIsNotDuplicated_Void() {

        //given
        final Member member = new Member(new Email(TEST_EMAIL), null, null);
        given(memberRepository.findByEmail(member.getEmail())).willReturn(null);

        //when
        memberHelperService.verifyEmailIsDuplicated(member.getEmail());

        //then
    }


    @Test(expected = MemberNotFoundException.class)
    public void findById_MemberDoesNotExist_MemberNotFoundException() {

        //given
        final long memberId = 0L;
        given(memberRepository.findOne(memberId)).willThrow(new MemberNotFoundException());

        //when
        memberHelperService.findById(memberId);

        //then

    }

    @Test
    public void findById_MemberExists_Member() {

        //given
        final Member mockMember = new Member(new Email(TEST_EMAIL), null, null);
        given(memberRepository.findOne(mockMember.getId())).willReturn(mockMember);

        //when
        final Member member = memberHelperService.findById(mockMember.getId());

        //then
        Assert.assertThat(member.getEmail(), is(member.getEmail()));

    }

}