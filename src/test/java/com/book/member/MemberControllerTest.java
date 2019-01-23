package com.book.member;

import com.book.member.domain.Email;
import com.book.member.domain.Member;
import com.book.member.domain.Name;
import com.book.member.domain.Password;
import com.book.member.dto.MemberSignupRequest;
import com.book.member.service.MemberHelperService;
import com.book.member.service.MemberSignUpService;
import com.book.test.BaseControllerTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@WebMvcTest(MemberController.class)
public class MemberControllerTest extends BaseControllerTest {

    @Autowired
    private WebApplicationContext context;

    @MockBean
    private MemberHelperService memberHelperService;
    @MockBean
    private MemberSignUpService memberSignUpService;


    private final String TEST_PASSWORD = "password";
    private final String TEST_FIRST_NAME = "test";
    private final  String TEST_LAST_NAME = "test";
    private final  String TEST_EMAIL = "test@test.com";

    private MockMvc mvc;

    @Before
    public void setUp() {
        mvc = buildMockMvc(context);
    }

    @Test
    @DisplayName("이메일양식이 유효하지 않습니다.")
    public void signupIsFail() throws Exception {

        //given
        final MemberSignupRequest request = buildSignupRequest("ansatgol", "password", "test", "test");

        //when
        mvc.perform(post("/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest());
        //then
    }

    @Test
    @DisplayName("회원가입성공")
    public void SignupIsSuccess() throws Exception {

        //given
        final MemberSignupRequest request = buildSignupRequest(TEST_EMAIL, TEST_PASSWORD, TEST_FIRST_NAME, TEST_LAST_NAME);
        given(memberSignUpService.signUp(any())).willReturn(request.toMember());

        //when
        final String memberAsString = mvc.perform(
                post("/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        final Member member = objectMapper.readValue(memberAsString, Member.class);
        //then

        Assert.assertThat(request.getEmail(), equalTo(member.getEmail()));
        Assert.assertThat(request.getName(), equalTo(member.getName()));
    }


    @Test
    public void getUser_GetMember_Member() throws Exception {

        //given
        final Member mockMember = buildMember(TEST_EMAIL, TEST_PASSWORD, TEST_FIRST_NAME, TEST_LAST_NAME);
        given(memberHelperService.findById(mockMember.getId())).willReturn(mockMember);

        //when
        mvc.perform(
                get("/members/{id}", 1))
                .andDo(print())
                .andExpect(status().isOk());
        //then
    }

    private MemberSignupRequest buildSignupRequest(final String email, final String password, final String firstname, final String lastname) {
        return MemberSignupRequest.builder()
                .email(new Email(email))
                .password(new Password(password))
                .name(new Name(firstname, lastname))
                .build();
    }

    private Member buildMember(final String email, final String password, final String firstname, final String lastname) {
        return new Member(new Email(email), new Password(password), new Name(firstname, lastname));
    }


}