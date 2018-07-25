# Step03 - 회원가입 Controller 만들기 : 표현영역 효율적으로 관리하기

## 표현 영역

먼저 컨트롤러를 작성하기 전에 표현 영역에 대해 간단히 얘기해보겠습니다. 앞장에서 말한 표현 -> 응용(서비스) -> 도메인 -> 인프라스트럭처 영역에 시작입니다. 표현 영역은 클라이언트에 요청을 받아 알맞는 응용서비스를 호출한 후 결과값을 클라이언트에게 보여주는 역할을 합니다. 표현 영역은 애플리케이션 시작인 만큼 아주 중요한 역할에 영역이라고 생각합니다. 단순 컨트롤러로써 Request Data 값을 받아서 대충 서비스로 던지는 역할이라고 생각할 수도 있습니다. 

하지만 표현 영역에서 Request Data 값을 받을 때 아주 잘 받아야 합니다. 대충 받는 순간 그 뒤쪽 서비스 영역, 도메인 영역, 인프라스트럭처 영역이 힘들어지기 때문입니다. 특히 서비스 영역이 힘들어집니다. 뒤쪽 영역이 왜 힘들어지는지와 뒤쪽 영역이 안 힘들어지기 위한 노력들을 살펴볼 것입니다. 먼저 이장에서 어떤 얘기들을 할지 나열해봤습니다.

* Request값 DTO로 명시적으로 받아서 응용 계층으로 넘기기(명시적이지 않는 예(Map))
* 클라이언트로 부터 넘어오는 데이터 validate 확실하게 하기
* 표현계층에서 사용되는 객체들을 Service에 넘기지않기(ex : HttpServletRquest)
* Domain 객체 바로 프론트엔드로 넘기지 않기 

이장은 위의 내용을 기반으로 어떻게하면 효율적으로 표현계층을 다룰지 얘기해볼것입니다.

## 1. Request값 DTO로 명시적으로 받아서 응용 계층으로 넘기기
먼저 Request 값을 명시적으로 받자입니다. 이 문제는 같이 협업하는 사람과 서비스 계층이 힘들어지기 때문입니다. 컨트롤러단에서 HttpServletRequest 값을 통해서 Request 바디 값을 받거나, Map을 이용하거나, RequestParam으로 모든 데이터를 받아서 처리하게 되면 그 당시 소스를 코딩할 할 때는 기억하지만 금방 그 데이터들이 어떤 용도에 데이터들인지 잊어버리기 때문입니다. (물론 RequestParam 같은 경우에는 필요에 따라 사용하면 매우 유용합니다.)

```java
//Map으로 받는예제
 public Map<String, Object> signUpMember(@RequestBody Map<String, Object> params){
     
     //something
 }
```

 조금이라도 다행인것은 Map을 표현 계층단에서 Value 값을 꺼내서 서비스단으로 넘겨주는것입니다. 만약 컨트롤러에서 받은 Map을 그대로 그냥 서비스단으로 넘기고 그것이 비지니스로직으로 이어진다면 아마 끔찍할 것입니다. 그 이유는 유지 보수와 가독성, 테스트 등이 매우 힘들어지기 때문입니다.

예를 들어 다른 개발자가 그것을 유지 보수한다고 가정해보겠습니다. 유지 보수하는 사람은 그 Map이 어떻게 사용되는지 모르기 때문에 하나하나의 키값 등을 추적해야 이 Map의 Request 데이터들을 명세할 수 있습니다.

심지어 다음과 같은 상황이 발생할 수도 있습니다. 클라이언트로부터 넘겨받은 값을 Map을 이용하여 아래 소스와 같이 키값이 state인 value 꺼낸다고 해보겠습니다. 클라이언트에 요구 사항으로 인해 이 키값이 status로 바뀌었습니다. 그렇게 되면 서비스 단과 컨트롤러에서 2군 대 이상 쓰인다고 하면 이것을 하나하나 다 찾아서 고쳐줘야 합니다. 이것은 type형이 아닌 String 값이기 때문에 추적하기도 힘듭니다. 뭐 물론 전체 검색해서 찾을 수도 있지만 프로젝트 내에 같은 단어가 있으면 이 또한 매우 까다롭게 됩니다.

 ```java
 params.get("state");
 ```

그렇기 때문에 아래와 과같이 명시적으로 dto를 만들고 데이터를 받는 게 좋습니다. 이렇게 명시적으로 정의하게 되면 유지 보수하는 사람 입장에서도 아 회원가입을 하기 위해서는 이러한 값들은 필수고 이러한 값은 선택인 것을 알 수 있습니다. 또한 요구 사항 등 변경에 의해 데이터가 추가되거나 하더라도 유연하게 변경할 수 있습니다. 위에서 말한 Request body key 값이 변경된다고 해도 해당 키값에 변수이기 때문에 IDE를 이용해 전체 변경을 하면 되기 때문에 아주 쉽게 변경할 수 있습니다.

다음은 회원가입을 하기위해 RequestDto를 정의한것입니다.
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
다음은 클라언트로부터 넘어오는 값에 대해 필수 값과 값의 형태를 표현 계층에서 확실하게 validate를 진행해야 표현 계층 이후에 계층들이 안 힘들어진다는 것입니다. 만약 표현 계층에서 필수 값, 값의 형식에 대한 validate 검사들이 제대로 되지 않는다면 서비스 계층과 도메인 계층은 계속해서 비즈니스로 직을 짜는 도중에 NULL 체크를 하는 소스를 작성해야 합니다. 이렇게 되면 비즈니스 로직을 집중할 수 없게 되고 코드의 가독성이 떨어지면서 버그로 이어질 가망성이 큽니다.

예를 들어 다음과 같은 소스가 서비스 계층 전방, 심지어는 비지니스 로직 중간중간에 이어질 수 있습니다. 이렇게 되면 가독성 뿐만 아니라 유지 보수하기 매우 난해해 집니다. 

```java
public void signUpUser(final String email,final String password, final String  firstname, final String lastname){
    if(email == null) throw new NullPointerException("email is null!");
    if(password == null) throw new NullPointerException("password is null!");
    if(firstname == null) throw new NullPointerException("firstname is null!");
    if(lastname == null) throw new NullPointerException("lastname is null!");
}
```

물론 응용서비스를 실행하는 주체가 같은 응용서비스이거나, 파라미터로 전달받은 값이 불안정하다면 응용 계층에서 validate 처리를 해야 할 수도 있습니다. 하지만 이 문제도 처음 표현 계층에서부터 무결한 데이터를 받거나, 응용서비스 자체에서도 NULL을 지양하는 코드를 작성해나간다면 응용서비스 내에 validate 코드 자체를 많이 줄여 나가실 수 있습니다.

이러한 무결한 데이터를 보장하기 위해 표현 계층에서부터 NULL이 들어오는 것을 방어해야 합니다. NULL을 방어하기 위해서는 다음과 같이 ReuqestData에 대한 Validate 어노테이션을 사용하는 방법이 있습니다. @NotEmpty라는 어노테이션을 사용하게 되면 NULL이나 ""값들이 서비스단으로 들어오게 하는 것을 막을 수 있습니다. 또 이외에 어노테이션을 커스터마이징해서 값의 크기 또는 형태가 안 맞는 것을 응용 계층으로 들어가는 것을 막을 수 있습니다.

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
위 NotEmpty, Email어노테이션을 사용하게 되면 어노테이션에 대한 invalid 한값이 넘어오면 프론트엔드로 BadRequest 처리를 하게 됩니다. 

>앞장에서도 말했지만 Email을 클래스로 선언해놨기 때문에 Email을 사용하는 RequestDto들은 이 클래스를 재활용해서 사용할 수 있게 됩니다. 깨알이지만 이렇듯 클래스를 잘 빼놓으면 응집력 있는 코드를 작성할 수 있습니다. 컨트롤러 코드를 작성하면 다음과 같이 될 수 있겠습니다.

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
위에 매개변수로 받는 errors에 request에 대한 dto를 validate 하는 과정 중에 에러를 가지고 있다면 프론트엔드에게 BadRequest를 보낼것입니다. 여기에서 error을 매개변수로 받지 않는다면 자동으로 프론트엔드하테 BadRequest를 보낼것입니다.


## 3. 표현계층에서 사용되는 객체들을 응용 계층에 넘기지않기(ex : HttpServletRequest, HttpServletResponse, HttpSession)

다음으로 얘기할 것은 HttpServletRequest, HttpServletResponse 와같이 표현 계층의 오브젝트 등을 Service 단으로 넘기지 말자입니다. 이렇게되면 응용 계층이 표현 계층에 의존하게 되기 때문입니다. 표현 계층의 데이터들을 받은 서비스는 서비스에서 필요로 하는 데이터를 가공해서 로직을 수행할 것입니다. 예를 들어 IP 정보, Locale 정보 등이 있을 수 있습니다. 이러한 값들을 표현 계층에서 데이터나 객체로 뽑아서 주는 게 아닌 응용 계층에 바로 HttpServletRequest를 넘기게 되면 응용 계층은 재사용하기가 매우 까다로 질 것입니다. 

예를 들어 주문서비스에서 유저의 HttpSession 값을 기반으로 id를 꺼내서 주문을 만들었다고 가정해보겠습니다. 그런데 이번 요구 사항 추가로 인해 유저의 HTTP 요청이 아닌 배치잡으로 주문을 만들어야 합니다. 아마 주문서비스 계층은 재사용을 하기 매우 까다로울 것입니다. 처음부터 표현 계층으로부터 HttpSession 객체가 아닌 id 값을 받아서 주문을 생성했다면 아마 쉽게 주문 서비스를 재사용할 수 있었을 것입니다.

또한 테스트 코드 작성하기가 매우 까다로진다는것입니다. 테스트를 작성하기위해서 매번 Mock데이터로 HttpServletRequest, HttpServletResponse을 만들어줘야하기 때문입니다. 이러한 이유로 위와같은 객체들은 최대한 표현 계층에서 가공한뒤 

## 4. Domain 객체 바로 프론트엔드로 넘기지 않고 DTO 사용하기
도메인을 바로 클라이언트로 넘기게 되면 다음과 같은 문제점들을 가집니다. 


* 도메인의 중요한 데이터가 외부에 노출되어 질 수 있습니다

도메인 객체 자체를 리턴하게 되면 회원의 개인 정보, 패스워드 등이 로그, 클라이언트에 노출이 되어집니다. 물론 이러한 내용도 어노테이션으로 노출되는것을 막을 수 있습니다. 하지만 어노테이션은 실수할 확률이 크다고 생각합니다. 실수로 어노테이션을 을 안 하거나 어노테이션을 삭제하게 되면 이 문제에 대해 쉽게 인지할 수 없기 때문입니다.

반면 DTO를 사용하게 되면 현저히 실수할 확률이 줄어들게 됩니다. 어 로테이션의 방법은 반대로 클라이언트 등에 표시되지 않는 데이터에 대해 어 로테이션을 하는 반면 DTO는 필요로 하는 데이터를 필드에 추가하기 때문에 실수를 할 확률이 줄어들게 됩니다. 왜냐 화면에 보여야 할 데이터가 안 보이는 것은 쉽게 잡을 수 있기 때문입니다. 또 한 화면에 보여야 할 데이터가 안 보인다는 것은 대부분 Test code 등으로 잡히는 문제입니다.

* 회원 정보를 리턴시 무한순환참조 Exception 발생하게 됩니다.

예를 들어 Member를 조회하는 기능이 있다고 해보겠습니다. 이 Member를 클라이언트로 넘겨주기 위해서는 직렬 화가 되어야 합니다. 문제는 멤버와 주문 관계상 서로를 참조하는 관계가 될 수 있다는 것입니다. 그렇게 되면 직렬화하는 과정에서 Member와 주문이 서로 계속 참조하게 되어 무한 순환 참조 Exception이 발생하게 되기 때문입니다.
뮬론 이러한 문제를 어노테이션등을 통해 방지할 수 도 있습니다. 하지만 아래와 같은 이슈들 때문에 개인적으로는 DTO를 사용하는것을 추천해드립니다.

* 같은 도메인이라도 return 값이 다를 수 있다.

예를 들어 유저와 어드민이 유저 정보를 조회해야 할 경우가 있을 것입니다. 하지만 여기에서 유저는 단순히 자기 유저 정보에 대해서만 조회가 되어야 하고, 반면 어드민이 조회하게 될 경우에 아이디가 현재 정지 상태인지 활성화 상태인지 여부도 조회되어야 한다고 해보겠습니다. 만약 여기에서 도메인에 어노테이션을 통해 무한 순환 참조 문제를 해결하려고 했다면 아마 문제가 생길것 입니다. 이렇게 되면 별도의 DTO를 가져가는 게 효율적일 것입니다.

다음 회원가입후 Member 객체를 리턴하기 위해 사용되는 DTO입니다.
```java
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberResponse {

    private Email email;
    private Name name;

    public MemberResponse(Member member) {
        this.email = member.getEmail();
        this.name = member.getName();
    }
}

```

## 마치며
표현 계층에 대해 간단하게 알아보고 어떻게 하면 효율적으로 표현 계층을 관리할지 알아보았습니다. 위에서 말했듯이 표현 계층에서는 필수 값, 값의 형식 등을 검증을 확실히 해야만 응용서비스에서 반복된 로직이 나오지 않습니다. 그리고 응용 계층은 NULL에 대한 걱정, 데이터 format에 대한 걱정을 하지 않게 됨으로써 소스의 비지니스로직에 집중할 수 있고, 가독성이 높아집니다.

물론 응용서비스를 실행하는 주체가 같은 응용서비스이거나, 파라미터로 전달받은 값이 불안정하다면 응용 계층에서 validate 처리를 해야 할 수도 있습니다. 하지만 처음부터 무결한 데이터를 받거나, 응용서비스 자체에서도 최소한 NULL을 지양하는 코드를 작성해나간다면 NULL에 대한 검사 코드는 거의 없어질것입니다. 다른장에서 어떤식으로 NULL을 지양해나가는지 알아보겠습니다.
