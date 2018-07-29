# Step-04 회원가입 Service 만들기 : 응용영역 효율적으로 관리하기 - Part2

# 응용 영역(서비스)
이번장은 회원가입기능을 구현하면서 ``응용 영역``을 어떻게하면 응집력있고, 변경에 유연하고, 가독성 좋은 코드를 작성할지에 대해 생각볼것입니다. 여기에서는 크게 아래와 같은 주제로 응용영역을 효율적으로 관리하는 방법에 대해 알아보겠습니다.


* 서비스크기
* DTO사용하기
* SRP(단일책임원칙)
* 캡슐화(Tell, don't ask)

## 5. 책임

**좀 더 책임의 의미를 잘 전달하기 위해 위에 캡슐화로 리팩토링 되어진 회원가입 코드가 아닌 처음 초난감 회원가입 서비스를 기반으로 이 단락을 설명하겠습니다.**


책임의 기준은 요구사항을 기반으로 누구에의해 이 기능이 변경되어질 수 있는가를 생각하는것입니다. 이변경은 곧 `책임`을 의미하기 때문입니다. 각각의 책임들은 하나의 서비스 또는 클래스로 분리되어야합니다. 그래야 변경에 유연하고 응집력있는 코드를 가질 수 있기 때문입니다. 사실 회원가입 기능에서 각각의 책임을 찾아내기위해서는 요구사항 분석단계에서 구현할 기능들을 나열하면서 생각을 했어야합니다.

>Tip -  처음에는 요구사항에 나열되어진 기능들로 바로 책임을 분리하는게 어려울 수 있습니다. 이럴경우 Test코드를 작성하면서 책임을 찾아내는게 좋은방법일 수 있습니다(이과정은 다음장에서 알아보도록 하겠습니다.)

다음은 왜 변경사항을 책임이라고하고 또 분리되어야하는지 예제를 통해 알아 보겠습니다.

### 책임 찾기
먼저 회원가입 서비스에 어떤 `책임`들이 있는지 생각해보겠습니다. `회원가입`, `모바일 인증` 2가지 책임이 있을 수 있습니다.

회원가입에 대한 로직에 변경이 일어나야한다면 모바일 인증이라는 코드와 결합되어 있어 변경하는 로직이 쉽지 않을것입니다. 반대로 인증 코드를 인증하는부분에 인증코드이외에도 만료시간등을 체크해야 한다고 하면 전반적인 회원가입 소스 로직을 이해한후에야 비로소 인증 코드에 대한 로직을 수정할 수 있을것입니다. 단지 회원가입이 아닌 인증코드 정책에 대한 소스 수정인데도 불구하고 회원가입을 이해해야하는 상황이 발생하는것입니다.

>Email 중복 여부 체크같은경우에는 이메일 중복체크라는 기능이 변경이 일어날 수 없다고 생각하기때문에 책임의 부분에서 뺏습니다. 하지만 이곳저곳 클래스에서 Email을 디비에서 검색시 null인지 검사하는 중복소스가 발생할 수 있습니다. 이부분은 밑에 단락에서 다루도록 하겠습니다.

만약 다른클래스에서도 모바일인증코드 기능을 구현해서 쓰고 있다면 사용 하는 곳을 모두 변경해줘야할 것입니다. 이렇듯 클래스가 하나의 책임만을 다루지 않게되면 변경에 엄청나게 취약한 구조가 되는것을 알았습니다. 그렇기 때문에 회원가입과 인증 코드를 확인하는 소스는 다음과 같이 별도의 서비스로 분리 해야할것입니다.

```java

// MemberSignUpService
public Member signUp(final MemberSignupRequest request){

    final Member duplicator = memberRepository.findByEmail(email);
    if(duplicator != null) throw new MemberDuplicationException();

    mobileVerificationService.verify(request.getMobile(),request.getAuthCode());
    Member member = request.toMember();//성공
    memberRepository.save(member);
    return member;

}


//MobileVerificationService
public void verify(final String mobile, final String authCode) {
    final Verification verification = mobileVerificationRepository.findByMobile(mobile);
    if(!verification.getAuthCode().equals(request.getAuthCod))) throw new MobileAuthenticationCodeFaildException();
}
```

MemberSignUpService에서 인증기능의 책임을 분리한 구조입니다. 이렇게되면 회원가입 로직이 변경되거나 모바일 인증 로직이 변경된다고해도 서로에 영향을 주지 않게 되었습니다. 이제 인증정책이 변경된다거나 회원가입 로직이 변경된다고해도 서로의 책임만 다해준다면 둘한테 영향을 주지 않습니다. 사실이것만으로도 기능면만 놓고보면 코드의 응집력 + 코드 변경에대한 유연성이 높아졌다고 할 수 있습니다. 물론 여기에 Verification  도메인에 캡슐화되어진 verify()함수를 사용한다면 더욱더 변경에 유연하 코드가 될것입니다.

아래는 리팩토링되어진 회원가입이 기능입니다. 이제 회원가입을 하기위해 아까와 같이 복잡합 인증코드의 로직을 이해할필요가없어졌습니다. 회원가입에 어떤 변화가 생긴다고해도 회원가입에대한 로직에 집중할 수 있게 되었습니다.

```java

public Member signUp(final MemberSignupRequest request){
    final Member duplicator = memberRepository.findByEmail(email);
    if(duplicator != null) throw new MemberDuplicationException();
    mobileVerificationService.verify(request.getMobile(), request.getAuthCode());
    Member member = request.toMember();
    memberRepository.save(member);
    return member;
}
```

// 응용서비스는 직접 로직을 수행하기보다는 도메인 모델에 로직 수행을 위임한다. 그리고 각각의 모듈들을 연결하는 역할을 합니다.


## 도메인 HelperService 만들기
위에서 설명하지 않은 부분이 있습니다. 바로 Email중복 체크를 하는 로직입니다. 여기에서 이메일 중복체크 여부는 사실 변경이 되어질 수 없는 기능 입니다. 단순히 디비에서 해당 Email이 있는지 확인하는 로직입니다. 아래와같이 사용해도 크게 무방합니다.

```java
final Member duplicator = memberRepository.findByEmail(email);
if(duplicator != null) throw new MemberDuplicationException();
```
하지만 아무래도 Email 중복체크를 하기위해서는 전방위적으로 유저를 조회하고 null인지 검사하는 로직이 중복되어야하는 문제가 있습니다.

그래서 이렇게 변경이 없고 단순 조회이거나, 조회 후 NULL체크하는 코드들은 해당 도메인에 helperService를 통해 관리하는 방법이 있습니다. 소스로하면 아래와같을 수 있습니다.

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


위와 같이 HelperService를 사용하게되면 null을 검사하는 반복 로직을 제거할 수 있습니다. 이 클래스가 바로 3장에서 말했던 부분인 NULL을 지양하. 바로 NULL을 지양하는 코드입니다. 위 소스에서 findById후 널이면 그냥 MemberNotFoundException()을 던지는 부분이 있습니다. 사실 디비로부터 null을 받은 후 null을 리턴해봤자 그 후에 어디에선가 NullPointerException이 발생하게 되어있습니다. 그렇게 되면  더 디버깅 하기가 힘들어집니다. 그렇기 때문에 차라리 조회하자마자 NULL인지 검사를 하고 바로 NullPointerException를 던지는게 더 디버깅하기 효율적입니다.


장점은 3장에서 말한 NULL과예를들어 다음과 같은 소스가 있을 수 있습니다.

```java
//MemberSignUpService
    public Member signUp(final MemberSignupRequest request){

        final Member duplicator = memberRepository.findByEmail(email);
        if(duplicator != null) throw new MemberDuplicationException();

        Member member = request.toMember();
        memberRepository.save(member);
        return member;
    }
```

위소스에서 처음에 이메일이 중복된 유저가 있는지 확인해주는 부분이 있습니다. 하지만 이렇게 email이 중복되었어? 객체한테 묻고 뭔가 행동을 하기보다는 객체한테 시키는게 소스코드의 응집성이나 가독성이 올라가게 됩니다.

예를들어 유저의 프로필 수정으로인해 이메일 중복 체크를 한다면 어딘가에서 저 email 중복체크소스가 또 사용되어질것이기때문입니다. 하지만 한곳에서 verify를 진행하게되면 error code + 동일한 Exception으로 처리할수 있게 됩니다.

Member같은경우 데이터베이스 영향없이 혼자 중복검사를 하기 어렵습니다. 그렇기 때문에 MemberService를 두고 MemberService 에서 간단한 verify, 조회용도로 사용하게되면 코드의 응집력을 높일 수 있습니다.


## 6. DTO 사용하기
메소드에 전달할 파라미터가 많다면 dto를 사용하는것을 추천드립니다. 그이유는 표현계층에서 말했던 이유와 비슷합니다.
1. 해당 메소드의 파라미터가 명세화가 되어집니다.
2. 추가적인 파라미터가 있다면 유연하게 대응할 수 있습니다.


그리고 메소드에 여러개의 파라미터를 던지는것은 순서도 신경써야되고 관리가 힘들어지기 때문입니다. 다음과 같이 사용하실 수 있으실것입니다.

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
        return new Member(email,password, name);
    }
}

//MemberSignUpService
    public Member signUp(final MemberSignupRequest request){

        memberHelperService.verifyEmailIsDuplicated(request.getEmail());
        Member member = request.toMember();
        memberRepository.save(member);
        return member;
    }
```

## 마치며
사실 서비스에서 중요한건 위에 내용 이외에도 SOLID 원칙에서 말하는것들을 준수할수록 좋은 디자인이 되어지는것같습니다.좀 더 좋은 디자인이 되어지기 위해 이장에서 다른 원칙들에 에대한 내용을 정리하는게 맞지만 내용이 너무 길어지는것같아서 정리를 하다 지웠습니다. 이 장에서 다루기에는 너무 많은 내용이 될것같습니다. 아마 단일 책임원칙을 외에 다른 원칙들은 다른장에서 내용들을 다뤄보도록 하겠습니다.

//
