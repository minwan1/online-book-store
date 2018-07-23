# 온라인 서점 API 만들기로 살펴보는 Spring OOP : 1.회원가입 Controller 만들기(표현영역 효율적으로 관리하기)

## 표현 영역

먼저 컨트롤러를 작성하기전에 표현 영역에 대해 간단히 얘기해보겠습니다. 앞장에서 말한 표현 -> 응용(서비스) -> 도메인 -> 인프라스트럭처 영역에 시작입니다. 시작인 만큼 아주 중요한 역할에 영역이라고 생각합니다. 단순 컨트롤러로써 Request값을 받아서 대충 서비스로 던지지 라고 생각할 수 도 있습니다. 하지만 표현 영역에서 Request 값을 받을 때 아주 잘받아야합니다. 대충받는순간 그 뒤쪽 서비스영역, 도메인영역, 인프라스트럭처 영역이 힘들어지기 때문입니다. (무엇보다 같이 일하는 개발자분들이 힘들어집니다ㅠ) 특히 서비스 영역이 힘들어집니다. 먼저 어떤 얘기들을할지 나열 해봤습니다.

* Request값 명시적으로 받기(명시적이지 않는 예(Map))
* validate 검사확실하게 하기
* 표현계층에서 사용되는 객체들을 Service에 넘기지않기(ex : HttpServletRquest)
* Domain 객체 바로 프론트엔드로 넘기지 않기 


## 1. Request값 dto로 명시적으로 받기
먼저 Request값을 명시적으로 받자입니다. 이문제는 같이 협업하는 사람과 서비스계층이 힘들어지기 때문입니다. 컨트롤러단에서 HttpServletRequest값을 통해서 Request 바디값을 받거나, Map을 이용하거나, RequestParam으로 모든 데이터를 받아서 처리하게되면 그당시 소스를 코딩할 할 때는 기억하지만 금방 그데이터들이 어떤 용도에 데이터들인지 읽어버리기때문입니다. (물론 RequestParam같은경우에는 필요에 따라 사용하면 매우 유용합니다.)
```java
 public Map<String, Object> signUpUser(@RequestBody Map<String, Object> params){
     //something
 }
```

 그나마 다행인것은 이것을 표현 계층단에서 그 Body에 값을 꺼내서 서비스단으로 넘겨주는것입니다. 만약 이것을 받은 Map그대로 그냥 서비스단으로 넘기고 그것이 비지니스로직으로 이어진다면 아마 끔찍할것입니다. 그이유는 유지보수와 가독성, 테스트등이 매우 힘들어지기 때문입니다. 

 예를들어 다른 개발자가 그것을 유지 보수한다고 가정해보겠습니다. 유지 보수하는 사람은 그 Map에 대한 정보를 모르기 때문에 하나하나 추적을 해야합니다. 
 
 심지어 다음과 같은 상황이 발생할 수 도 있습니다. Map에서 키값 state로 클라이언트로부터 넘겨받은 값을 꺼낸다고 해보겠습니다. 클라이언트에 요구사항으로 인해 이 키값이 status로 바뀐다고 가정을 해보겠습니다. 그렇게 되면 서비스단과 컨트롤러에서 2군대이상 쓰인다고하면 이것을 하나하나 다찾아서 고쳐줘야합니다. 이것은 type형이 아니기 때문에 추적이 힘들기 때문입니다. 뭐 물론 전체검색해서 찾을 수 도 있지만 프로젝트내에 같은 단어가 있으면 매우 까다롭게 됩니다.

 ```
 params.get("state");
 ```

그렇기 때문에 다음과 같이 명시적으로 dto를 만들고 데이터를 받는게 좋습니다. 이렇게 명시적으로 정의하게 되면 유지보수하는 사람입장에서도 아 회원가입을 하기위해서는 이러러한값들이 필수고 클라이언트로부터 이런 값들을 받는구나라고 바로 알 수 있게 됩니다. 위에서 말한 Request body key값이 변경된다고해도 해당 키값에 변수만 IDE를 이용해 전체 변경을 하면되기 때문에 아주 쉽게 변경할 수 있습니다.
```java
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberSignupRequest {

    @Valid
    private Email email;
    @Valid
    private Name name;
    @Valid
    private Password password;

    public Member toMember(){
        return new Member(email, password, name);
    }
}
```

## 2. 클라이언트로 부터 넘어오는 데이터 validate 확실하게 하기
다음은 클라언트로부터 넘어오는 값을 프리젠테이션에서 확실하게 validate를 해야한다는것입니다. 그이유는 서비스 영역이 매우 힘들어지기 떄문입니다. 서비스 영역과 도메인영역은 비지니스로직을 수행해야하는데 중간 중간 NULL검사를 해야하기 때문입니다. 예를들어 다음과 같은 소스가 이어질 수 있다는 얘기 입니다. 이렇게 되면 가독성 뿐만아니라 유지보수하기 매우 난해해지기 때문입니다.
```java
public void signUpUser(final String email,final String password, final String  firstname, final String lastname){
    if(email == null) throw new NullPointerException("email is null!!")
    if(password == null) throw new NullPointerException("password is null!!")
    if(firstname == null) throw new NullPointerException("firstname is null!!")
    if(lastname == null) throw new NullPointerException("lastname is null!!")
}
```

물론 어쩔수 없는 값들도 있겠지만 적어도 필수값에 대해서 NULL인지 검사하는 로직은 없어야한다고 생각하기 때문입니다. 뭐 이외에도 email형식인지 검사하는 로직등이 있을 수 있겠습니다. 이러한 validate 검사는 표현계층과 응용 계층에 신뢰라고 생각합니다. 물론 표현 계층에서만 서비스단을 호출하는것은 아니겠지만요. 나중 연재에서 말하겠지만 서비스단에서도 최대한 NULL을 지양할 예정입니다. 

계속 이야기를 하면 앞에서 말한대로 표현계층에서부터 NULL이 들어오는 것을 방어해야합니다. NULL을 방어하기위해서는 다음과 같은 코드를 작성할 수 있겠습니다. @NotEmpty라는 어노테이션을 사용하게 되면 NULL이나 ""값들이 서비스단으로 들어오게 하는것을 막습니다.

```java

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class Email {

    //모든곳에서 다체크해야하는 이슈발생.
    @NotEmpty
    @org.hibernate.validator.constraints.Email
    @Column(name = "email", nullable = false, unique = true)
    private String value;
    
}

```
보통 그이후에 valid한값이 넘어오지 않았기 때문에 프론트엔드로 BadRequest처리를 하게됩니다. 앞장에서도 말했지만 Email을 클래스로 선언해놨기 때문에 Email을 사용하는 RequestDto들은 이 클래스를 재활용해서 사용할 수 있게됩니다. 깨알이지만 이렇듯 클래스를 잘빼놓으면 응집력 있는 코드를 작성할 수 있습니다. 코드를 작성하면 다음과 같이 될 수 있겠습니다.

```java
    @RequestMapping(value = "/members", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.CREATED)
    public ResponseEntity signUpMember(@RequestBody @Valid final MemberSignupRequest request, final BindingResult errors){
        if(errors.hasErrors()) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        final MemberResponse member = new MemberResponse(memberSignUpService.signUp(request));
        return new ResponseEntity<>(member, HttpStatus.CREATED);
    }
```
errors에 뭔가 에러를 가지고있다면 badrequest를 보낼것입니다. 여기에서 error을 매개변수로 받지 않는다면 자동으로 프론트엔드하테 BADREQUEST를 보낼것입니다.


## 3. 표현계층에서 사용되는 객체들을 응용 계층에 넘기지않기(ex : HttpServletRequest, HttpServletResponse, HttpSession)
다음으로 얘기할것은 HttpServletRequest, HttpServletResponse등을 Service단으로 넘기지 말자입니다. 그러면 그것을 받은 서비스는 필요로하는 데이터를 만들어서 로직을 수행할것입니다. 예를들어 IP정보 정보, Locale등이 있을 수 있습니다. 이러한 값들을 객체로 뽑아서 주는게 아닌 HttpServletRequest를 넘기게되면 응용 계층은 재사용하기가 매우 까다로 질것입니다. 예를들어 기존에는 유저의 HttpSession 값을 기반으로 Id를 꺼내서 주문을 만들었다고 가정해보겠습니다. 그런데 이것을 실제 유저의 주문이 아닌 배치잡으로 돌아가야한다고 한다면 아마 그 서비스 계층은 재새용을 하기 매우 까다로울 것입니다.

또한 테스트 코드 작성하기가 매우 까다로진다는것입니다. 테스트를 작성하기위해서 매번 Mock데이터로 HttpServletRequest, HttpServletResponse을 만들어줘야하기 때문입니다. 

## 4. Domain 객체 바로 프론트엔드로 넘기지 않기 
도메인을 바로 클라이언트로 넘기게되면 다음과 같은 문제점들을 가집니다. 

* 회원 정보를 리턴시 무한순환참조 Exception 발생하게 됩니다.

예를들어 Member를 조회하는 기능이 있다고 해보겠습니다. 이 Member를 클라이언트로 넘겨주기 위해서는 직렬화가 되어야합니다. 문제는 멤버와 주문관계상 서로를 참조하는 관계가 되어질 수 있다는것입니다. 그렇게 되면 직렬화 하는 과정에서 Member와 주문이 서로 계속 참조하게되어 무한순환참조 Exception이 발생하게 되기 때문입니다.
뮬론 이러한 문제를 어노테이션등을 통해 방지할 수 있습니다.

* 도메인의 중요한 데이터가 외부에 노출되어 질 수 있습니다

도메인 객체 자체를 리턴하게되면 회원의 개인정보, 패스워드등이 로그, 클라이언트에 노출이 되어집니다. 물론 이러한것들도 어노테이션으로 처리할 수 있지만 어노테이션을 실수로 안하거나 삭제하게 되면 그대로 회원의 개인정보가 노출된다는것입니다. 이러한 실수는 누구나 할 수 있기 때문입니다.
하지만 DTO를 리턴하게되면 최악의 경우 유저에대한 정보는 노출할지 않는다는것입니다. 어떤 DTO에대한 정보가 추가되어 있지 않으면 Test과정 또는 QA과정에서는 버그를 잡을 수 있기 때문입니다.


## 마치며
표현 계층에대해 간단하게 알아보고 어떻게하면 효율적으로 표현계층을 관리할지 생각해보았습니다. 표현 계층 소스를 어떻게 시작하느냐에 따라 뒤에소스가 클리코드가 되느냐 안되느냐가 결정이 되는것입니다. 