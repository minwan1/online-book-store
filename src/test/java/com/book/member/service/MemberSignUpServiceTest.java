package com.book.member.service;

import com.book.member.domain.Email;
import com.book.member.domain.Member;
import com.book.member.dto.MemberSignupRequest;
import com.book.member.repository.MemberRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;

@RunWith(MockitoJUnitRunner.class)
public class MemberSignUpServiceTest {


    @InjectMocks
    private MemberSignUpService memberSignUpService;

    @Mock
    private MemberHelperService memberHelperService;
    @Mock
    private MemberRepository memberRepository;

    private static final String TEST_EMAIL = "ansatgol@gmail.com";


    // 메소드네임 / 테스트하고자하는 형태 / 예상되어지는 행동

    //    isAdult_AgeLessThan18_False
    //    withdrawMoney_InvalidAccount_ExceptionThrown
    //    admitStudent_MissingMandatoryFields_FailToAdmit

    // 이메일중복체크를 한다.
    // 데이터를 디비에 넣는다.

    @Test(expected = MemberDuplicationException.class)
    public void signUp_EmailIsDuplicated_MemberDuplicationException() {

        //given
        final MemberSignupRequest request = buildRequest(TEST_EMAIL);
        doThrow(new MemberDuplicationException()).when(memberHelperService).verifyEmailIsDuplicated(request.getEmail());

        //when
        final Member member = memberSignUpService.signUp(request);

        //then

        //throw MemberDuplicationException.class
    }

    @Test
    public void signUp_SignUpIsSuccess_Member() {

        //given
        final MemberSignupRequest request = buildRequest(TEST_EMAIL);
        given(memberRepository.save(any(Member.class))).willReturn(request.toMember());

        //when
        final Member member = memberSignUpService.signUp(request);


        //then
        Mockito.verify(memberRepository, atLeast(1)).save(any(Member.class));
        Assert.assertThat(member.getEmail(), is(request.getEmail()));

    }

    private MemberSignupRequest buildRequest(final String email) {
        return MemberSignupRequest.builder().email(new Email(email)).build();
    }
}