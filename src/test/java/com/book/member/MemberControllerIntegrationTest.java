package com.book.member;

import com.book.member.domain.Email;
import com.book.member.domain.Member;
import com.book.member.domain.Name;
import com.book.member.domain.Password;
import com.book.member.dto.MemberSignupRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@SpringBootTest
public class MemberControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    private ObjectMapper mapper;

    private MockMvc mockMvc;

    private final  String TEST_PASSWORD = "password";
    private final  String TEST_FIRST_NAME = "test";
    private final  String TEST_LAST_NAME = "test";
    private final  String TEST_EMAIL = "test@test.com";

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .build();
    }

    @Test
    @Transactional
    public void signUpMember_SignupIsSuccess_Member() throws Exception {

        //given
        final MemberSignupRequest request = buildSignupRequest(TEST_EMAIL, TEST_PASSWORD, TEST_FIRST_NAME, TEST_LAST_NAME);

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

        Assert.assertThat(request.getEmail(), is(member.getEmail()));
        Assert.assertThat(request.getName(), is(member.getName()));
    }

    private MemberSignupRequest buildSignupRequest(final String email, final String password, final String firstname, final String lastname) {
        return MemberSignupRequest.builder()
                .email(new Email(email))
                .password(new Password(password))
                .name(new Name(firstname, lastname))
                .build();
    }


}