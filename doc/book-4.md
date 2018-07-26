# Step-04 회원가입 Service 만들기 : 응용영역 효율적으로 관리하기

# 응용계층(서비스)
이번장은 회원가입기능을 구현하면서 ``응용 영역`` 어떻게하면 응집력있고 가독성 좋은 코드를 짜는지와 어떻게 관리하면 유지보수하기좋은 ``응용 영역``이 될지에 대해 생각볼것입니다. 여기에서는 크게 아래와같은 주제로 다루어볼것입니다.


* 서비스크기
* DTO사용하기
* SRP(단일책임원칙)
* Tell, don`t ask


## 1. 서비스크기

드디어 회원가입을 하기 위한 signUp 서비스를 구현할 차례입니다. 먼저 회원가입 서비스 기능을 구현하기전에 회원가입 서비스를 어느정도의 서비스 크기로 가지갈지 생각해봐야합니다. 


## 도메인에 모든 기능을 한클래스에서 구현하는 방법
먼저 책에서 많이 나오는 방법인  MemberService.Class 구현 기반의 도메인에 모든 기능을 한클래스에서 구현하는 방법이 있을것입니다.

```java
// MemberService.Class

@Autowired
private MemberRepository memberRepository;
// 유저에관한 부가적인 기능을 만들기위한 많은 의존성 주입들

public Member signUp(MemberSignupRequest request){ // 회원가입을 위한 member정보
    //비지니스로직
}
// 유저에 관한 기능들

```
만약 위와 같은 방법으로 구현하게 되면 유저에 대한 기능들이 한클래스내에 있어서 좋습니다. 여기에 1 인터페이스와 1구현체로 가져가게된다면 모든 Memberr의 기능들이 인터페이스에 명세되는 장점 있을 수 있습니다.

## 2. 기능별로 서비스 클래스를 구현하는방법
그리고 다른 방법으로 기능별로 서비스클래스를 구현하는 방식이 있을 수 있습니다. 
```java
@Service // 어노테이션을 붙이면 스프링에서 ComponatScan을 통해서 알아서 스프링 컨테이너에 빈을등록해줌.
@AllArgsConstructor // 모든 필드 변수에대해 생성자를 만들어줌으로 모든멤버필드에 스프링 빈이 주입이 되어집니다.
public class MemberSignUpService { // 회원가입이라는 *책임*

    private final MemberRepository memberRepository; // 클래스 책임에 맞는 의존성 주입들
    private final MemberService memberService; // 이서비스는 밑에서 왜 이서비스가 필요한지에대해 설명이 되어집니다.

    public Member signUp(final MemberSignupRequest request){ // 회원가입을 위한 member정보

        memberService.verifyEmailIsDuplicated(request.getEmail());
        Member member = request.toMember();
        memberRepository.save(member);
        return member;
    }

}

````
하지만 첫번째 방법에는 단점이 많다고 생각합니다. 몇가지 단점을 설명해보겠습니다.

1. **테스트코드들을 구분하기 어려워진다는것입니다.** 한클래스에 너무많은 기능들이 있기 때문에 이것을 한클래스에 테스트코드를 작성하게되면 어떤게 어떤메소드에 테스트코드인지 구분하기가 어려워질 수 있습니다. 물론 테스트 클래스만 여러개로 나눌 수 있지만 이 또한 어디에 어떤테스트가 작성되었는지 찾기가 어렵다는 문제가 있습니다.
만약 ``기능별로 서비스 클래스를 구현하는방법``인 MemberSignUpService로 선언하고 테스트 클래스를 작성했다면 쉽게 테스트 코드를 찾고 관리할 수 있을것입니다. 이유는 MemberSignUp에 관련된 테스트 소스는 MemberSignUpTest 에만 있을것이기 때문입니다. 

2. **서로 관련없는 코드들이 너무 뒤엉키면서 한 클래스에 너무많은 의존성들이 주입되어 있다는것입니다.** 관련 없는 public 함수들과 private 함수둘이 뒤엉켜 소스의 가독성을 떨어뜨리기 때문입니다. 만약 명시적인 기능의 클래스를 선언해 서비스를 관리한다면 public함수 그리고 private 함수가 모두 해당클래스에 관련있는 함수들일것입니다. 뿐만 아니라 관련없는 의존성주입들이 한클래스에 주입되어지는것을 막을것입니다. 

3. **결국 추상화되어지기 위해서는 특정 기능의 서비스로 분리될수밖에 없습니다.** 예를들어 유저패스워드변경 기능, admin에 의한 패스워드 변경 기능이 있다고 해보겠습니다. 그렇게되면 변경자체는 admin에 의해 되어질수도 있고, 유저에의해 패스워드가 변경되어질 수 있습니다. 좀 더 자세히 설명하면 Member가 패스워드를 변경하기위해서는 자신의 패스워드를 인증해야 하고 Admin은 별다른 인증없이 회원의 패스워드를 변경할수 없다고 가정해보겠습니다. 결국 이것은 서로 다른 변경을 의미할것입니다. 그리고 그것은 곧 다른 책임이라고 생각합니다. 이러한 다른 책임은 결국 추상화로써 분리 할 수 밖에없을것입니다.
 결국 다형성을 주기위해 ``도메인에 모든 기능을 한클래스에서 구현하는 방법``의 MemberService.class 에서 분리해 나와 별도의 클래스로 분리하고 인터페이스를 만들것입니다. 반면 2번째 방법의 기능에 따른 서비스 구현 방식 같은경우 쉽게 유저패스워드 변경이라는 인터페이스에 녹아들어 다형성을 줄 수 있을것입니다. 이러한 내용은 다른 장에서 좀더 해보겠습니다.  


## 3. SRP(단일책임원칙) 

위에서 예기했던 `책임`에 대해 좀 더 얘기를 해보려고합니다. ``응용(서비스) 계층``을 설계할때 항상 생각해야할것이 있습니다. 첫 번째로 요구사항을 받고 요구사항을 정리하는것입니다. 간단한것도 정리하는것을 추천드립니다. 간단해 보이지만 간단하지 않을 수 있기 때문입니다.

그리고 두 번째로 누구에의해 이 기능이 변경되어질 수 있는가를 생각하는것입니다. 이변경은 곧 ``책임``을 의미하기 때문입니다. 각각의 책임들은 한클래스로 분리되어야합니다. 왜 변경사항을 책임이라고하고 또 분리되어야하는지 예제를 통해 봐보겠습니다.

다음과 같은 요구사항이 있다고 가정해보겠습니다.

1. 유저를 가입시키는 기능을 구현한다.
2. 가입을 하려면 모바일 인증을해야한다.
3. 중복된 이메일은 회원을 가입할 수 없다.

```java

@Service 
@AllArgsConstructor 
public class MemberSignUpService {

    private final MemberRepository memberRepository;
    private final MemberService memberService;
    private final MobileVerificationService mobileVerificationService;

    public Member signUp(final MemberSignupRequest request){

        final Member duplicator = memberRepository.findByEmail(email);
        if(duplicator != null) throw new MemberDuplicationException();

        final Verification verification = mobileVerificationService.getVerification(request.getMobile()); // 유저가 있는지 여부를 확인할 수 있는데 인증코드 정보 객체

        if(verification.getAuthCode().equals(request.getAuthCod))){
            Member member = request.toMember();//성공
            memberRepository.save(member);
            return member;
        }else{
            throw new MobileAuthenticationCodeFaildException();
        }
    }


}

```

위 소스의 흐름은 이렇습니다.
1. 먼저 이메일이 중복되었는지 Email로 멤버를 조회하여 멤버의 존재여부를 확인을 합니다.
2. 유저가 모바일 인증한 정보의 객체를 가져옵니다.
3. 유저의 모바일정보가 인증되었는지 확인을합니다.
4. 성공했다면 회원가입을 시킵니다.


먼저 이클래스에는 어떤 ``책임``들이 있는지 생각해보겠습니다. 그렇습니다 ``회원가입``, ``모바일 인증`` 2가지 ``책임``이 있습니다. 회원가입에 대한 로직에 변경이 일어나야한다면 모바일 인증이라는 코드와 결합되어 있어 변경하는 로직이 쉽지 않을것입니다. 반대로 인증 코드를 인증하는부분에 인증코드이외에도 만료시간등을 체크해야 한다고 하면 전반적인 회원가입 소스 로직을 이해한후에야 비로소 인증 코드에 대한 로직을 수정할 수 있을것입니다. 단지 회원가입이 아닌 인증코드 정책에 대한 소스 수정인데도 불구하고 회원가입을 이해해야하는 상황이 발생하는것입니다.

>Email 중복 여부 체크같은경우에는 이메일 중복체크라는 기능이 변경이 일어날 수 없다고 생각하기때문에 책임의 부분에서 뺏습니다. 하지만 이곳저곳 클래스에서 Email을 디비에서 검색시 null인지 검사하는 중복소스가 발생할 수 있습니다. 이부분은 밑에 단락에서 다루도록 하겠습니다.


만약 다른클래스에서도 모바일인증코드 기능을 구현해서 쓰고 있다면 사용 하는 곳을 모두 변경해줘야할 것입니다. 이렇듯 클래스가 하나의 책임만을 다루지 않게되면 변경에 엄청나게 취약한 구조가 되는것을 보셨습니다. 그렇기 때문에 회원가입과 인증 코드를 확인하하는 소스는 다음과 같이 별도의 서비스로 분리 해야할것입니다.


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


//MobileVerificationService.class
public void verify(final String mobile,final String authCode) {
    final Verification verification = mobileVerificationRepository.findByMobile(mobile);
    if(!verification.getAuthCode().equals(request.getAuthCod))) throw new MobileAuthenticationCodeFaildException();
}
```

MemberSignUpService에 인증기능의 책임을 분리한 구조입니다. 이렇게되면 회원가입 로직이 변경되거나 모바일 인증 로직이 변경된다고해도 서로에 영향을 주지 않게 되었습니다. 이제 인증로직이 변경된다거나 회원가입 로직이 변경된다고해도 서로의 책임만 다해준다면 둘한테 영향을 주지 않게되었습니다. 사실이것만으로도 가독성 + 코드의 응집력이 높아졌다고 할 수 있습니다.

## 4. Tell, don`t ask
서비스계층에서 또 다른 중요한포인트는 객체한테 묻지말고 직접 시켜야한다고 생각합니다. 이렇게 되지 않으면 어플리케이션 전체적으로 중복 코드가 늘어나면서 유지보수하기 힘들어지기 때문입니다. 분기처리가 각각 객체에 책임에 맞게 분기가 되어있으면 경우의 수를 생각할게 크게 없는데 한곳에 if문이 여러개가 있게되면 가독성이 떨어지고 분기문 파악하는데도 오래 걸리게됩니다. 예를들어 아래와같은 예가 있을 수 있겠습니다.

예를들어 다음과 같은 소스가 있을 수 있습니다.

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

Service말고 다른 예를 하나 들면 Verification class가 있다고 가정해보겠습니다. 이것은 사실 위에서도 다뤘던 예제이기도합니다.

```java

public class Verification(){
    private long expireDate;
    private String authCode; // 어떤시스템으로부터발급되어진 인증코드

    private boolean isExpired(){
        //something
    }

    private boolean verify(String authCode){ // 클라언트로부터 입력될수있는 인증코드

        // 인증코드가 만료되거나 인증코드가 맞지않으면 Exception

    }
}


```

만약 어플리케이션 내에서 모바일인증 시스템을 인증을 처리하기위해 Verification객체로 인증을 처리한다고 가정해보겠습니다. 소스로보면 다음과 같을 수 있겠습니다.

```java
//PasswordChangeService
public void changePassword(final String mobile,final String authCode, final String password){
    Verification verification = mobileVerificationRepository.findByMobile(mobile);
    if(verification.getAuthenticationCode.eqauls(authCode) && verification.getExpireDate() < 현재시간){
        //성공
    }else{
        //실패
    }
}

```

위와 같이 되면 인증시스템 정책 변경에 의해서 만료시간이 더이상 체크할필요가없게 된다면 이 만료체크를 하는 소스가 반영되어있는곳은 모두 다 소스를 지워야할것입니다. 즉 변화에 취야한 소스가 되어집니다. 그나마 다행인건 아래처럼 인증 서비스에 구현을 하는것입니다. 그래도 뭔가 가독성이 떨어지는 감이 없지않아 있습니다.



그렇습니다. 아래와같이 깔끔하게 객체한테 시키게되면 아주 깔끔한 소스가 나오게됩니다. 훨씬더 가독성도 좋아졌습니다. 

```java
//
public void verify(final String mobile, final String authCode){
    MobileVerification verification = mobileVerificationRepository.findByMobile(mobile);
    verification.verify(authCode);
}
```

아래는 이렇게 리팩토링되어진 회원가입이 기능이 되어질것입니다. 이제 회원가입을 하기위해 아까와 같이 복잡합 인증코드의 로직을 이해할필요가없어졌습니다. 뭔가 회원가입에 변화가 생긴다고해도 회원가입에대한 로직이 집중할 수 있게 되었습니다.

```java


public Member signUp(final MemberSignupRequest request){ 
    mobileVerificationService.verify(request.getMobile(), request.getAuthCode());
    memberService.verifyEmailIsDuplicated(request.getEmail());
    Member member = request.toMember();
    memberRepository.save(member);
    return member;
}
```



## 5. DTO 사용하기
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

    public Member signUp(final MemberSignupRequest request){

        memberService.verifyEmailIsDuplicated(request.getEmail());
        Member member = request.toMember();
        memberRepository.save(member);
        return member;
    }
```


## 마치며
사실 서비스에서 중요한건 위에 내용 이외에도 SOLID 원칙에서 말하는것들을 준수할수록 좋은 디자인이 되어지는것같습니다.좀 더 좋은 디자인이 되어지기 위해 이장에서 다른 원칙들에 에대한 내용을 정리하는게 맞지만 내용이 너무 길어지는것같아서 정리를 하다 지웠습니다. 이 장에서 다루기에는 너무 많은 내용이 될것같습니다. 아마 단일 책임원칙을 외에 다른 원칙들은 다른장에서 내용들을 다뤄보도록 하겠습니다.







