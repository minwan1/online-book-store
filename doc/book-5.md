# Step-04 회원가입 Service 만들기 : 응용영역 효율적으로 관리하기 - Part2

# 응용 영역(서비스)
이번 단계는 `응용영역` 효율적으로 관리하기 - Part2 편으로 어떻게 하면 `응용영역`을 응집력 있고, 변경에 유연하고, 가독성 좋은 코드를 작성할지에 대해 알아볼 것입니다. 이장에서는 크게 아래와 같은 주제로 `응용영역`을 효율적으로 관리하는 방법에 대해 알아보겠습니다.


* 책임
* 도메인 HelperService 만들기
* DTO 사용하기
* 최종적인 회원가입 코드
* Swagger를 통한 회원 가입 API CALL

## 4. 책임(단일책임원칙)
객체지향 설계를 하는 데 있어 기준을 잡아주는 원칙들이 있는데 이것을 SOLID 원칙이라고 합니다. 이중 먼저 단일 책임 원칙에 대한 내용을 얘기하려고 합니다. 단일 책임 원칙은 클래스는 하나의 책임을 가져야 한다는 내용입니다. 왜 클래스는 하나의 책임을 가져야 하는지에 대해 알아보겠습니다.

>좀 더 책임의 의미를 잘 전달하기 위해 처음 작성한 초난감 회원가입 코드를 기반으로 이 단락을 설명하겠습니다.

**초난감 회원가입 서비스 코드**
```java

@Service
@AllArgsConstructor// 옆에 어노테이션을 통해 아래에 멤버필드들이 자동으로 스프링 빈으로 등록됨.
public class MemberSignUpService {

    private final MemberRepository memberRepository;
    private final CodeVerificationService codeVerificationService;

    public Member signUp(final MemberSignupRequest request){

        final Member duplicator = memberRepository.findByEmail(request.getEmail());
        if(duplicator != null) throw new MemberDuplicationException();

        final CodeVerification CodeVerification = codeVerificationService.findByMobile(request.getMobile()); // 유저가 있는지 여부를 확인할 수 있는데 인증코드 정보 객체

        if(codeVerification.getAuthCode().equals(request.getAuthCode())){
            Member member = request.toMember();//성공
            memberRepository.save(member);
            return member;
        }else{
            throw new MobileAuthenticationCodeFaildException();
        }
    }
}

```

### 책임의 중요성
먼저 책임이 뭔지 간단하게 알아보겠습니다. 클래스에서 책임의 기준은 요구 사항을 기반으로 누구에 의해 이 기능이 변경될 수 있는가를 생각하는 것입니다. 이 변경은 곧 `책임`을 의미하기 때문입니다. 각각의 책임들은 하나의 서비스 또는 클래스로 분리되어야 합니다. 그래야 변경에 유연하고 응집력 있는 코드를 가질 수 있기 때문입니다. 변경에 유연함은 곧 버그가 없고 안정적인 코드를 의미합니다.

사실 처음 회원가입 요구 사항 분석 단계에서 구현할 기능들을 나열하면서 각각의 책임들을 분리하는 게 좋습니다. 또 개인적으로는 이러한 이유 때문에 기능 구현에 앞서 구현할 기능들을 나열해 책임들을 정리하는편입니다.

> -  처음에는 요구 사항에 나열된 기능들로 바로 책임을 분리하는 게 어려울 수 있습니다. 이럴 경우 Test 코드를 작성하면서 책임을 찾아내는 게 좋은 방법일 수 있습니다. (이과정은 다음 장에서 알아보도록 하겠습니다.)

다음은 왜 변경사항을 책임이라고 하고 또 분리되어야 하는지 예제를 통해 알아보겠습니다.

### 책임 찾기
먼저 회원가입 서비스에 어떤 `책임`들이 있는지 생각해보겠습니다. `회원가입`, `모바일 인증` 2가지 책임이 있을 수 있습니다.

초난감 회원가입 코드를 기반으로 보면 회원가입에 대한 로직이 변경이 일어난다면 모바일 인증이라는 코드와 결합되어 있어 변경하는 로직이 쉽지 않습니다. 반대로 인증 코드 부분을보면 인증코드 이외에도 만료시간 등을 체크해야 한다고 하면 전반적인 회원가입 소스 로직을 이해한 후에야 비로소 인증 코드에 대한 로직을 수정할 수 있습니다. 단지 회원가입이 아닌 인증코드 정책에 대한 소스 수정인데도 불구하고 회원가입을 이해해야 하는 상황이 발생하게 됩니다.

>Email 중복 여부 체크 같은 경우에는 이메일 중복체크라는 기능이 변경이 일어날 수 없다고 생각하기 때문에 책임의 부분에서 뺏습니다. 하지만 이곳저곳 클래스에서 Email을 디비에서 검색 시 NULL 인지 검사하는 중복 소스가 발생할 수 있습니다. 이 부분은 밑에 단락에서 다루도록 하겠습니다.

### 책임 분리
만약 다른 클래스에서도 모바일 인증코드 기능을 구현해서 쓰고 있다면 사용하는 곳을 모두 변경해줘야 할 것입니다. 이렇듯 클래스 또는 서비스가 하나의 책임만을 다루지 않게 되면 변경에 엄청나게 취약하다는것을 알게되었습니다.

그렇기 때문에 회원가입과 인증 코드를 확인하는 소스는 다음과 같이 별도의 서비스로 분리해야 할 것입니다.

```java

// MemberSignUpService
public Member signUp(final MemberSignupRequest request){

    final Member duplicator = memberRepository.findByEmail(email);
    if(duplicator != null) throw new MemberDuplicationException();

    codeVerificationService.verify(request.getMobile(),request.getAuthCode());
    Member member = request.toMember();//성공
    memberRepository.save(member);
    return member;

}


//CodeVerificationService
public void verify(final String mobile, final String authCode) {
    final CodeVerification codeVerification = codeVerificationRepository.findByMobile(mobile);
    if(!codeVerification.getAuthCode().equals(request.getAuthCod))) throw new MobileAuthenticationCodeFaildException();
}
```

MemberSignUpService에서 인증 기능의 책임을 분리한 구조입니다. 이렇게 되면 회원가입 로직이 변경되거나 모바일 인증 로직이 변경된다고 해도 서로에 영향을 주지 않게 됩니다. 이제 인증 정책이 변경된다거나 회원가입 로직이 변경된다고 해도 서로의 책임만 다해준다면 둘한테 영향을 주지 않습니다.

사실 이것만으로도 기능면만 놓고 보면 코드의 응집력 + 코드 변경에 대한 유연성이 높아졌다고 할 수 있습니다. 물론 여기에 앞에 part1에서 다루었던 CodeVerification의 캡슐화된 verify() 함수를 사용한다면 더욱더 변경에 유연한 코드가 될 것입니다.

### 모바일 인증과 책임을 분리한 회원가입 메소드

아래는 리팩토링되어진 회원가입이 기능입니다. 이제 회원가입을 하기위해 아까와 같이 복잡합 인증코드의 로직을 이해할필요가없어졌습니다. 회원가입에 어떤 변화가 생긴다고 해도 회원가입에 대한 로직에만 집중하면 되니까 좀 더 과감하게 개발할 수 있게 되었습니다.

```java

public Member signUp(final MemberSignupRequest request){
    final Member duplicator = memberRepository.findByEmail(email);
    if(duplicator != null) throw new MemberDuplicationException();
    codeVerificationService.verify(request.getMobile(), request.getAuthCode());
    Member member = request.toMember();
    memberRepository.save(member);
    return member;
}
```

## 5.도메인 HelperService 만들기
위에서 설명하지 않은 부분이 있습니다. 바로 Email 중복 체크를 하는 로직입니다. 여기에서 이메일 중복체크 여부는 사실 변경이 되거나 확장될 일이 거의 없습니다. 단순히 디비에서 해당 Email이 있는지 확인하는 로직이기 때문입니다.(물론 캐시 등을 적용할 수 도 있을 것입니다.) 사실 위에서 사용한 것처럼 아래와 같이 사용해도 크게 무방합니다.

```java
final Member duplicator = memberRepository.findByEmail(email);
if(duplicator != null) throw new MemberDuplicationException();
```
하지만 아무래도 Email 중복체크를 하기 위해서는 전방위적으로 유저를 조회하고 NULL 인지 검사하는 로직이 중복되어야 하는 문제가 있습니다

그래서 이렇게 변경이 없고 단순 조회이거나, 조회 후 NULL 체크하는 코드들은 해당 도메인에 helperService를 통해 관리하는 방법이 있습니다. 코드로 보면 아래와 같을 수 있습니다.

```java

//MemberHelperService

public Member findById(final long id){
    final Member member = memberRepository.findOne(id);
    if(member == null) throw new MemberNotFoundException();
    return member;
}

public void verifyEmailIsDuplicated(final Email email){
    if(isEmailDuplicated(email)) throw new MemberDuplicationException();
}
```


위와 같이 HelperService를 사용하게 되면 아이디 중복체크 외에도 특정 필드로 객체를 조회한 후 NULL을 검사하는 반복 로직들을 해당 클래스에 추가할 수 있습니다.

### NULL을 지양하기
위 클래스가 바로 3장에서 말했던 부분인 NULL을 지양하게 도와주는 역할을 하는 클래스입니다. 위 소스에서 findById 후 NULL이면 그냥 MemberNotFoundException()을 던지는 부분이 있습니다. 사실 디비로부터 NULL을 받은 후 NULL을 리턴해봤자 그 후에 어디에선가 NullPointerException이 발생합니다. 그렇게 되면 오히려 더 디버깅하기 힘들어집니다. 그렇기 때문에 차라리 조회하자마자 NULL 인지 검사를 하고 해당 조회 id를 조회 하고 바로 NullPointerException를 던지는 게 더 디버깅하기 효율적입니다. NULL은 최대한 주지도 말고 받지도 말아야합니다.


## 6. DTO 사용하기
메서드에 전달할 파라미터가 많다면 dto를 사용하는 것을 추천드립니다. 그 이유는 표현 계층에서 말했던 이유와 비슷합니다.
1. 해당 메서드의 파라미터가 명세화가 됩니다.
2. 추가적인 파라미터가 있다면 유연하게 대응할 수 있습니다.


## 7. 최종적인 회원가입 코드

```java
@Service
@AllArgsConstructor // 옆에 어노테이션을 통해 아래에 멤버필드들이 자동으로 스프링 빈으로 등록됨.
public class MemberSignUpService {

    private final MemberRepository memberRepository;
    private final MemberHelperService memberHelperService;

    public Member signUp(final MemberSignupRequest request){
        memberHelperService.verifyEmailIsDuplicated(request.getEmail());
        Member member = request.toMember();
        memberRepository.save(member);
        return member;
    }

}
```
* 모바일 인증부분은 예제 설명을 위해 추가한 부분이라 코드에서 제거했습니다.

**드디어 회원가입 API가 완성되었습니다. 위에 소스를 보시면 아시겠지만 응용서비스는 직접 로직을 수행하기보다는 도메인 모델에 로직 수행시키거나 각각의 모듈들을 연결하는 역할을 합니다. 그래야 서비스 영역은 단순한 구조를 가지게 되고, 가독성이 좋아지고, 그럼으로써 품질좋은 코드를 유지할 수 있게 해줍니다.**

API테스트는 아래와같이 테스트하 실 수 있습니다.

## Swagger 를 통한 회원 가입 API CALL
다음과 같이 Swagger 홈페이지를 통해 테스트 해보실 수 있있습니다.

![](https://github.com/minwan1/online-book-store/blob/master/img/swagger-test.gif)

* Swagger설치 방법은 [브록-1](https://github.com/minwan1/online-book-store/blob/master/doc/%EB%B8%8C%EB%A1%9D-1.md) 참조해주시면 감사하겠습니다.
* 다음은 Swagger Test URL입니다. - http://localhost:8080/swagger-ui.html

**회원가입 Request Body**
```json
{
  "email": {
    "value": "test@naver.com"
  },
  "name": {
    "firstname": "test",
    "lastname": "test"
  },
  "password": {
    "value": "12345"
  }
}
```
(try-it-out!)
**회원가입 Response Body**

```
{
  "email": {
    "value": "test@naver.com"
  },
  "name": {
    "firstname": "test",
    "lastname": "test",
    "fullName": "test test"
  }
}

```

## 마치며

회원가입 API가 드디어 만들어졌습니다. 사실 위에 초난강 회원가입 서비스처럼 생각 없이 의식의 흐름대로 코드를 작성한다면 30분 안에도 회원가입 기능을 구현할 수 있습니다. 하지만 그렇게 코드를 작성하게 되면 지금 당장은 돌아가겠지만 아마 후에 그 코드를 유지 보수하기 위해 300분 이상에 시간을 쓰고 있을 것입니다. 아주 간단한 기능 변경인데도 엄청난 리소스가 들어가게 되고, 간단한 확장인데도 코드를 손댈 수 가없어 꼼짝할 수 없는 상태가 됩니다.

이러한 일이 발생하지 않도록 코드 하나를 작성하더라도 변경, 확장을 생각하면서 코드를 만들어야 합니다. 그래야 기능이 변경되더라도 유연하게 변경하고, 확장해나갈 수 있기 때문입니다. 이러한 기준을 잡게 도와주는 원칙이 위에서 말한 SOLID 원칙입니다.

위에 예제 코드에서도 좀 부족한 부분이 있습니다. 확장 부분이나, codeVerificationService 클래스에 너무 의존하고 있는 모습들이 대표적인 예입니다. 이러한 문제들은 어떻게 풀어가는지에 대한 예제는 주문 API를 만들면서 알아보도록 하겠습니다.

아마 다음 장에서는 주문 API를 만들기 전에 테스트 코드에 대한 내용을 다룰 것 같습니다. 긴 내용 읽어주셔서 감사합니다.(__)
