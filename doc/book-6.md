# Step-05 : 테스트 코드 작성하기

# 목차
<!-- TOC -->

- [1. 테스트 코드의 필요성](#1-%ED%85%8C%EC%8A%A4%ED%8A%B8-%EC%BD%94%EB%93%9C%EC%9D%98-%ED%95%84%EC%9A%94%EC%84%B1)
- [2. Test code 작성 방법](#2-test-code-%EC%9E%91%EC%84%B1-%EB%B0%A9%EB%B2%95)
    - [Junit](#junit)
    - [Mockito](#mockito)
- [3. Unit Test(단위 테스트)](#3-unit-test%EB%8B%A8%EC%9C%84-%ED%85%8C%EC%8A%A4%ED%8A%B8)
    - [컨트롤러 테스트(예제)](#%EC%BB%A8%ED%8A%B8%EB%A1%A4%EB%9F%AC-%ED%85%8C%EC%8A%A4%ED%8A%B8%EC%98%88%EC%A0%9C)
    - [서비스 테스트(예제)](#%EC%84%9C%EB%B9%84%EC%8A%A4-%ED%85%8C%EC%8A%A4%ED%8A%B8%EC%98%88%EC%A0%9C)
- [4. Integration Test(통합 테스트)](#4-integration-test%ED%86%B5%ED%95%A9-%ED%85%8C%EC%8A%A4%ED%8A%B8)
    - [통합테스트 예제](#%ED%86%B5%ED%95%A9%ED%85%8C%EC%8A%A4%ED%8A%B8-%EC%98%88%EC%A0%9C)
        - [@RunWith(SpringRunner.class)](#runwithspringrunnerclass)
        - [@SpringBootTest](#springboottest)
        - [MockMvc 사용하기](#mockmvc-%EC%82%AC%EC%9A%A9%ED%95%98%EA%B8%B0)
    - [TDD](#tdd)
- [5. 마무리하며](#5-%EB%A7%88%EB%AC%B4%EB%A6%AC%ED%95%98%EB%A9%B0)

<!-- /TOC -->
# 1. 테스트 코드의 필요성
회원가입 기능을 완성했지만 기능을 테스트하기 위해서는 Postman이나 Swagger로 테스트를 하거나 프론트엔드 화면을 구성해서 테스트를 해야 합니다. 기능 테스트 하나를 해야 하더라도 Postman, Swagger, 프런론 엔드 구성을 해야 한다면 너무 불편할 것입니다. 하지만 테스트코드를 가지고 있다면 아주 간단하게 기능을 테스트해볼 수 있습니다. 이외에도 아래와 같은 장점들이 있습니다.


* 단위 테스트로 개발한다면 스프링이 실행될 때까지 기다리지 않아도 되니까 생산성이 올라간다.
* 테스트 코드가 있기 때문에 리팩토링을 할 때 자신감 있게 리펙토링이 가능해진다.
* 특정 기능을 리팩토링 한 후 그 기능과 관련된 테스트 코드들이 있으면 사전에 사이드 이펙트들을 방지할 수 있다.
* 테스트 코드를 작성하다 보면 이상하게 테스트 코드를 작성하기가 어려운 경우가 있는데 이것은 해당 기능 이상의 기능을 하고 있을 수 있다는 것을 의미한다. 이 부분을 개선해 나간다면 Decouple된 시스템을 갖게 된다.
* 실제 서비스에 배포되기 전에 테스트 코드들을 통해 기능들을 자동 테스트할 수 있다.
* 테스트 코드를 작성한다면 사전에 생각하지 못한 논리적인 오류들을 생각할 수 있다.



# 2. Test code 작성 방법
테스트 코드를 작성하기 위해서는 Junit 테스팅 프레임워크와 Mockito 목 프레임워크를 이용해 테스트 코드를 작성할 것입니다. 테스트 방법은 단위 테스트와 통합 테스트 두 방법으로 나누어 테스트 코드를 작성할 것입니다. 
여기에서 단위 테스트는 퍼블릭 메서드 기능 하나를 기준으로 테스트합니다. 통합 테스트는 컨트롤러 - 서비스 - 리파지토리 영역까지 테스트를 기준으로 테스트합니다. 먼저는 Junit부터 간단히 알아보겠습니다.

## Junit
JUnit은 테스팅 프레임워크입니다. 외부에 의존 없이 자바 코드만으로 테스트를 가능하게 해주는 프레임워크입니다.

## Mockito
Mockito는 자바에서 테스트를 하기 위해 Mock을 만들어주는 프레임워크입니다. Mockito은 우리가 테스트하는 과정에서 이메일이 나가지 않거나 데이터베이스와 같은 외부 시스템 데이터베이스 연결에 의존하지 않게 해주는 아주 유용한 프레임워크입니다.
또한 Mockito는 given, when, then 절로 테스트 코드를 좀 더 직관적으로 작성할 수 있습니다. 먼저 Junit과 Mockito를 이용해 단위테스트부터 작성해보겠습니다.


# 3. Unit Test(단위 테스트)
단위 테스트는 테스트하고자 하는 가장 작은 기능 단위로 테스트하는 기법을 말합니다. 단위 테스트 코드를 작성하는 이유는 가장 빠르게 최소 단위의 기능을 간단하게 테스트해볼 수 있고 정확하게 동작하는지 알 수 있습니다.

또한 해당 클래스의 책임또는 기능의 단위를 잡아주는 좋은 기준이 됩니다. 테스트 코드를 작성하다 보면 이상하게 테스트 코드를 작성하기가 어려운 경우가 있습니다. 이것은 해당 기능의 책임 이상의 기능을 하고 있을 가망성이 큽니다. 그렇기 때문에 새로운 디자인을 생각하게 해줍니다.

저는 개인적으로 단위 테스트는 크게 서비스 영역과 컨트롤러 영역 두개로 나눠서 테스트를 합니다. 먼저 컨트롤러 테스트 코드를 작성하겠습니다.

## 컨트롤러 테스트(예제)
단위 테스트에서는 스프링에 모든 빈들을 올릴 필요가 없습니다. 테스트하고자 하는 빈 만 등록하게 되면 훨씬 더 빠르게 테스트할 수 있습니다. 만약 기능 테스트 하나를 위해서 애플리케이션 빈들을 다 올리게 되면 엄청난 시간 낭비 일 것입니다.

아래 소스는 테스트 코드를 작성하기 위해 설정하는 값에 대한 간단한 설명입니다.

1. @RunWith(MockitoJUnitRunner.class)은 주어진 mock 객체들을 초기화하는 역할을 합니다.
2. 테스트할 때 필요로 하는 Mock 객체들을 등록해줍니다. 
3. 테스트하고자 하는 클래스에 @InjectMocks을 넣어주면 Mock 객체들이 해당 클래스에 주입됩니다.
4. 컨트롤러 객체를 전달받아 MockMvc 객체를 생성하며, 다른 스프링 빈을 올릴 필요 없이 해당 컨트롤러만 테스트 가능합니다.

```java

@RunWith(MockitoJUnitRunner.class) //1
public class MemberControllerTest {

    @Mock
    private MemberHelperService memberHelperService;//2

    @InjectMocks
    private MemberController memberController; //3

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders //4
                .standaloneSetup(memberController)
                .build();
    }
    ...

}
```

그리고 다음은 Member 컨틀롤러를 테스트하기 위한 테스트 코드입니다. 개인적으로 테스트하기 위해서 given, when, then을 정하고 순서에 맞게 작성하는 편입니다.
|구분|설명|
|-|-|
|given|목데이터 또는 Request 값들을 설정합니다.|
|when|실제 테스트 하고자하는 기능을 실행합니다.|
|then|테스트한 결과값이 의도한값이 맞는지 검증합니다.|

간단하게 아래 테스트 코드를 설명해드리겠습니다.

1. 회원가입을 위해 RequestBody 값을 만든다.
2. 컨트롤러 쪽에서 memberSignUpService를 호출할 때 Mock 데이터인 request.toMember() 값을 리턴해주는 코드입니다.
3. 실제 MemberController에 /members POST를 호출하고 결과 값을 받습니다.
4. 받은 값을 Mapper를 이용해 Member 클래스로 역 직렬화를 합니다.
5. 리턴 값이 의도한 값이 맞는지 확인합니다.

```java

    private String TEST_EMAIL = "test@test.com";
    private String TEST_PASSWORD = "password";
    private String TEST_FIRST_NAME = "test";
    private String TEST_LAST_NAME = "test";

    @Test
    public void signUpMember_SignupIsSuccess_Member() throws Exception {

        //given
        final MemberSignupRequest request = buildSignupRequest(TEST_EMAIL, TEST_PASSWORD, TEST_FIRST_NAME, TEST_LAST_NAME); //1
        given(memberSignUpService.signUp(any())).willReturn(request.toMember()); //2

        //when
        final String memberAsString = mockMvc.perform(
                post("/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();//3
        final Member member = mapper.readValue(memberAsString, Member.class); //4

        //then
        Assert.assertThat(request.getEmail(), equalTo(member.getEmail())); //5
        Assert.assertThat(request.getName(), equalTo(member.getName()));
    }

    private MemberSignupRequest buildSignupRequest(final String email, final String password, final String firstname, final String lastname) {
        return MemberSignupRequest.builder()
                .email(new Email(email))
                .password(new Password(password))
                .name(new Name(firstname, lastname))
                .build();
    }

```
5번 목록을 보면 굳이 Member 클래스로 역질렬화하여 assertThat로 검증을 할 필요 없이 JsonPath() 기능을 이용해서 검증할 수 있지만 개인적으로는 String으로 테스트 코드들을 관리해야 한다는 점 때문에 클래스로 변경하여 데이터를 검증하는 편입니다.

Email이나 Name 이 같은 value 인지 체크하기 위해서는 Equals메소드를 재정의 해줘야 합니다. 그래야 같은 벨류값인지 비교를할 수 있습니다. 이러한 이유는 [여기](https://minwan1.github.io/2018/07/03/2018-07-03-equals,hashcode/)를 참조해주시면 감사하겠습니다.
[Controller Test code](https://github.com/minwan1/online-book-store/blob/master/src/test/java/com/book/member/MemberControllerTest.java)

## 서비스 테스트(예제)

다음은 서비스 코드 단위 테스트 예제입니다. 여기에서는 서비스에 회원가입에 대한 로직만 정확하게 테스트하면 됩니다. 테스트 코드 작성 순서는 다음과 같습니다.
1. memberSignUpService.signUp() 넘어올 예상되는 파라미터 값을 만듭니다.
2. Mockito 프레임워크를 이용해 repository를 이용해 저장한 후 디비에서 리턴해줄 값을 작성합니다.
3. 실제 memberSignUpService.signUp() 호출합니다. 
4. 실제로 memberSignUpService.signUp() 기능이 호출돼서 서비스의 비즈니스 로직이 제대로 진행돼서 memberRepository.save가 호출되었는지 확인합니다.
5. memberSignUpService.signUp()로 처리된 이메일과 예상되는 이메일이 맞는지 확인합니다.

```java
    @Test
    public void signUp_SignUpIsSuccess_Member() {

        //given
        final MemberSignupRequest request = buildRequest(TEST_EMAIL); //1
        given(memberRepository.save(any(Member.class))).willReturn(request.toMember()); //2

        //when
        final Member member = memberSignUpService.signUp(request); //3

        //then 
        Mockito.verify(memberRepository, atLeast(1)).save(any(Member.class)); //4
        Assert.assertThat(member.getEmail(), is(request.getEmail()));//5

    } 
```

[Service Test code](https://github.com/minwan1/online-book-store/blob/master/src/test/java/com/book/member/service/MemberSignUpServiceTest.java)

# 4. Integration Test(통합 테스트)
통합 테스트는 단위 테스트보다는 덜 빈번하게 실행되면서 세세한 API 수준의 검증보다는 모듈 단위의 상호 연동이 문제가 없는지 검증하는 것이 주 목적입니다. 위에서 각각의 모듈 별로 단위 테스트를 성공했음에도 이것들을 하나로 결합해서 테스트를 하는 과정에서 테스트가 실패할 수도 있습니다. 예를 들어 데이터베이스 제약조건 등에 문제, 다른 클래스에 의존하고 있는 클래스들이 제대로 연결되었는지 등이 예일 수 있습니다.

## 통합테스트 예제
다음은 단위 테스트와 다르게 아래의 두 개의 어노테이션을 붙여주셔야 합니다. 위에 단위 테스트는 해당 클래스 하나만 테스트하면 됐지만 통합 테스트 같은 경우 테스트하고자 하는 모듈들이 모두 연결되어 있어야 하기 때문에 애플리케이션에 모든 빈들을 등록해서 테스트합니다.

```java
@RunWith(SpringRunner.class)
@SpringBootTest
public class MemberControllerIntegrationTest {
```

### @RunWith(SpringRunner.class)
SpringJUnit4ClassRunner를 상속받은 SpringRunner 클래스는 SpringBootTest 의 설정돼있는 정보들을 이용해서 스프링 컨테이너를 생성한다. 그리고 Test 클래스에 있는 필드에 있는 변수들에 맞는 클래스를 의존성 주입을 해준다.

### @SpringBootTest
@SpringBootTest은 프로젝트에 어플리케이션빈들은 등록해주는 역할을 한다. 반드시 위에 @RunWith(SpringRunner.class)과 같이 사용해줘야 한다.이 어노테이션을 t사용하게 되면 실제 스프링을 실행한 것과 같이 모든 빈이 올라오게 됩니다.


### MockMvc 사용하기
아래의 소스와 같이 단위 테스트와 달리 스프링 웹 애플리케이션을 빈을 주입받고 그것을 기반으로 MockMvc를 만들어줘야 합니다. 이렇게 설정을 하고 실제 API를 호출하게 되면 각 레이어들이 연결된 기능을 테스트해볼 수 있습니다.

```java
    @Autowired
    private WebApplicationContext webApplicationContext;

    @Before
    public void setUp() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .build();
    }
```
Mvc 설정을 했으면 이제 실제 테스트 코드를 작성해야 합니다. 순서를 설명해드리면 다음과 같습니다.
1. 회원가입 API를 호출하면 데이터베이스 회원정보가 insert 될 것 입니다. 하지만 test 코드에서 @Transactional 붙이면 테스트코드 실행 후 insert 됐던 데이터가가 롤백이 됩니다.
2. 회원가입을 하기 위해 필요한 RequestBody 값을 만든다.
3. 실제 회원가입 URL을 호출한다.
4. 결과값을 Member 클래스로 변경한다.
5. 결과값이 테스트 의도한값이 맞는지 확인한다.

```java

    @Test
    @Transactional//1
    public void signUpMember_SignupIsSuccess_Member() throws Exception {

        //given
        final MemberSignupRequest request = buildSignupRequest(TEST_EMAIL, TEST_PASSWORD, TEST_FIRST_NAME, TEST_LAST_NAME); //2

        //when
        final String memberAsString = mockMvc.perform(
                post("/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(); //3
        final Member member = mapper.readValue(memberAsString, Member.class); //4

        //then
        Assert.assertThat(request.getEmail(), is(member.getEmail()));//5
        Assert.assertThat(request.getName(), is(member.getName()));
    }

    private MemberSignupRequest buildSignupRequest(final String email, final String password, final String firstname, final String lastname) {
        return MemberSignupRequest.builder()
                .email(new Email(email))
                .password(new Password(password))
                .name(new Name(firstname, lastname))
                .build();
    }

```

[Integration Test code](https://github.com/minwan1/online-book-store/blob/master/src/test/java/com/book/member/MemberControllerIntegrationTest.java)

## TDD
TDD란 테스트 코드를 먼저 작성하고 기능을 구현하고 또 이것을 리팩 토핑 하고 이것을 반복해서 기능을 만드는 개발 방법론을 말합니다. 테스트 주도 개발을 하게 되면 장점은 테스트 코드 하기 좋은 코드를 만들게 된다는 것입니다. 테스트 코드가 작성하기 좋은 코드의 의미는 해당 메서드나 클래스가 하나의 기능 혹은 책임만을 하고 있다는 것을 의미합니다. 이렇듯 테스트 주도 개발을 하게 되면 자연스럽게 Decouple된 객체지향 설계를 할 수 있게 되고 자연스럽게 테스트 코드들을 얻을 수 있습니다. 그렇게 되면 처음에 말했던 테스트 코드의 장점들을 얻게 됩니다.




# 5. 마무리하며
회원가입에 관련된 테스트 코드들을 완성했습니다. 이제 회원가입의 기능을 테스트를 하기 위해서 Swagger나 프론트엔드 화면이 필요하지 않습니다. 테스트 코드들로 테스트가 가능하기 때문입니다. 또한 테스트 코드는 좋은 문서또는 커뮤니케이션 도구가 될 수 있습니다. 예를 들어 다른 개발자가 SMS 인증 기능이 필요해서 SMS 인증 기능을 만든 개발자한테 기능을 요구했으면 SMS 기능 개발자는 단순 문서나 설명만 해주기보다는 테스트 코드를 준다면 좀 더 효율적으로 커뮤니케이션을 할 수 있을것입니다.

참고
* [백명석님 강의](https://www.youtube.com/watch?v=60lLSe1phks&list=PLuLb6MC4SOvXCRePHrb4e-EYadjZ9KHyH)
