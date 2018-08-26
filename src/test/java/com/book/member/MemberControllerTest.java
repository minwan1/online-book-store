package com.book.member;

import com.book.member.domain.Email;
import com.book.member.domain.Member;
import com.book.member.domain.Name;
import com.book.member.domain.Password;
import com.book.member.dto.MemberSignupRequest;
import com.book.member.service.MemberHelperService;
import com.book.member.service.MemberSignUpService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(MockitoJUnitRunner.class)
public class MemberControllerTest {

    @InjectMocks
    private MemberController memberController;
    @Mock
    private MemberHelperService memberHelperService;
    @Mock
    private MemberSignUpService memberSignUpService;

    private MockMvc mockMvc;
    private ObjectMapper mapper;

    private final String TEST_PASSWORD = "password";
    private final String TEST_FIRST_NAME = "test";
    private final  String TEST_LAST_NAME = "test";
    private final  String TEST_EMAIL = "test@test.com";


    @Before
    public void setUp() {
        mapper = new ObjectMapper();
        mockMvc = MockMvcBuilders
                .standaloneSetup(memberController)
                .build();
    }

    @Test
    public void signUpMember_EmailFormatIsInvalid_BadRequest() throws Exception {

        //given
        final MemberSignupRequest request = buildSignupRequest("ansatgol", "password", "test", "test");

        //when
        mockMvc.perform(
                post("/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest());
        //then
    }

    @Test
    public void signUpMember_SignupIsSuccess_Member() throws Exception {

        //given
        final MemberSignupRequest request = buildSignupRequest(TEST_EMAIL, TEST_PASSWORD, TEST_FIRST_NAME, TEST_LAST_NAME);
        given(memberSignUpService.signUp(any())).willReturn(request.toMember());

        //when
        final String memberAsString = mockMvc.perform(
                post("/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        final Member member = mapper.readValue(memberAsString, Member.class);
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
        mockMvc.perform(
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