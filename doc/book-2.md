# 온라인 서점 API 만들기로 살펴보는 Spring Boot OOP : 회원 도메인 정의


## 1. 회원 도메인 정의  
먼저 앞에서 설계 테이블을 간단히 보겠습니다. 다음과 같은 member 테이블이 있었습니다.

![](https://i.imgur.com/Aa1gDEY.png)

회원을 가입시키기 위해서는 member 도메인을 정의해야할 필요가 있습니다. 위 그림 테이블을 기반으로 도메인을 정의해볼것입니다. 

```java
@Entity
@Table(name = "member")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Member  {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Embedded
    private Email email;
    @Embedded
    private Password password;
    @Embedded
    private Name name;

    @CreationTimestamp
    @Column(name = "created_dt", nullable = false, updatable = false)
    private Timestamp createdAt;

    @UpdateTimestamp
    @Column(name = "updated_dt", nullable = false )
    private Timestamp updatedAt;

    public Member(final Email email, final Password password, final Name name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }
}
```
간단하게 위에 정의되어있는 어노테이션들이 무엇인지 알아보고, 아래에서 세부적인 domain 클래스들이 어떻게 정의되었는지 알아보겠습니다.

### @Entity 
JPA에서 테이블과 매핑할 클래스는 @Entity 어노테이션을 붙여줘야합니다. 
### @Table
테이블어노테이션은 엔터티와 매핑할 실제 디비 테이블을 지정합니다. 다음은 member를 지정함으로써 테이블 member와 매핑이됩니다. jpa에서 테이블을 새로 만들어주는 기능이 있는데 이것을 이용해 테이블을 생성하면 name에 member를 기반으로 테이블을 만들어줍니다.

### @NoArgsConstructor(access = AccessLevel.PROTECTED)
다음은 파라미터가없는 기본생성자를 만들어주는 어노테이션입니다. Spring JPA를 사용하기위해서는 반드시 기본생성자가 필요합니다. Spring JPA는 DB에서 데이터를 읽어와 도메인에 매핑할 때 기본생성자를 사용해서 객체를 생성해 데이터를 매핑합니다. 그렇기 때문에 반드시 기본생성자가 필요합니다. java로 표현하면 다음과 같습니다.
```java
protected Member(){

}
```
여기에서 굳이 접근 레벨을 Protected로 한이유는 아무대에서나 도메인을 생성못하게 하려는 이유입니다. Member를 만들기 위해서는 기본적인 정보가있을텐데 기본생성자를 이용해서 객체를만드면 불안전한 객체가 생성되기때문이다. 또한 Protected로 하면 협업하는 개발자들이 적어도 이객체는 기본생성자로 생성하는 객체는 아니구나라고 생각할 수 있습니다. 그렇게되면 모두가 실수할 확률이 줄어들게됩니다.

### @Id
JPA에서 해당 필드에 기본키를 지정하기위해서 @Id 어노테이션을 지정해야합니다.
### @GeneratedValue(strategy = GenerationType.IDENTITY)
@GeneratedValue(strategy = GenerationType.IDENTITY) 지정하면 mysql의 AUTO_INCREAMENT와 같은기능으로 데이터베이스가 기본키를 생성해줍니다. 예를들어 회원을 2명생성해주면 자동으로 2번째 회원을 생성할때는 id를 2값으로 회원을 생성해줍니다.


### @Embedded 
Spring Data JPA Embedded어노테이션이 있음으로써 Spring Data JPA는 좀더 객체지향적으로 프로그래밍하게 해줍니다. 데이터타입을 규합시켜주거나 객체의 응집력을 높여주기 때문입니다. 

예를들어 아래와같은 Email클래스 있습니다. 이것은 MemberClass에 Embedded되어 있는 Email class입니다. 이렇게 클래스로 가지고있다고해도 JPA는 쉽게 데이터베이스 데이터를 저장해줍니다. 그리고 이렇게 email을 클래스로 가지고가게되면 많은 장점들을 가지게됩니다. 먼저 Email클래스에 어떠한 어노테이션들이 있는지 알아보고 장점을 알아보겠습니다.

Email 클래스에서 @org.hibernate.validator.constraints.Email 어노테이션과 @NotEmpty 어노테이션이 있습니다. 이 어노이션은 클라이언트로부터 넘어온 데이터가 email 형식이 맞는지 value의 값이 들어있는지 validation을 체크해주고 유효하지 않으면 Exception을 발생시켜 클라이언트한테 이메일형식이 유효하지 않다고 알려주는 역할을 합니다.

만약 Email을 클래스로 가지지 않고 단순히 String value로 Member class에 String으로 email을 가져갔다고 가정해보겠습니다. 그리고 수취인등이 Email을 디비에 추가해야한다고 가정해보겠습니다. 그러면 수취인 클래스에도 똑같이 이메일 validation체크등을 해주고 이메일에 필요한 부수적인 기능들을 구현해줘야할 것입니다. 여기에서 말하는 부수적인 기능이란 아래와같이 Host를 구해온다거나 id만 구해오는 메소드의기능들 입니다.

```java
@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@JsonIgnoreProperties({"host", "id"})
public class Email {

    //모든데에서 email인지 validation 다체크해야하는 이슈발생.
    @NotEmpty
    @org.hibernate.validator.constraints.Email
    @Column(name = "email", nullable = false, unique = true)
    private String value;

    public Email(final String value) {
        this.value = value;
    }

    public String getHost() {
        int index = value.indexOf("@");
        return value.substring(index);
    }

    public String getId() {
        int index = value.indexOf("@");
        return value.substring(0, index);
    }
}
```

이렇듯 embedded타입을 사용하면 많은 장점들을 가집니다. 하나 더 예를들어 아래의 Name클래스를 봐보겠습니다.
```java
@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Name {

    @NotEmpty
    @Column(name = "first_name", nullable = false)
    private String firstname;

    @NotEmpty
    @Column(name = "last_name", nullable = false)
    private String lastname;

    public String getFullName(){
        return this.firstname +" "+this.lastname;
    }


}

```

회사 제품이 갑자기 글로벌 시장 진출로인해 middle_name을 받아야한다고 가정해보겠습니다. 만약 프로젝트에서 DTO를 쓴다면 모든 전역지역에 middlename을 넣어줘야하는 문제가 생길것입니다. 하지만 name을 클래스로 가져갔다면 유연하게 변경할 수 있습니다. 그이유는 Name을쓰는곳은 하나의 클래스이기 때문입니다. 이렇듯 Name을 클래스로 가지가면 응집력이 올라가게 됩니다.




그렇다고해서 모든것을 Embedded 타입으로 가져가자는 의미는 아닙니다!! 의미있는 데이터끼리 또 확장성이 많은 데이터를 잘 선별하여 Embedded 타입으로 빼야한다고 생각합니다. 여기에서 나온 Vlidation어노테이션은 후에 좀 더 자세히 알아보겠습니다.



### @CreationTimestamp, @UpdateTimestamp
어노테이션은 만들어진 시간과 데이터 변경시간을 체크해주는 어노테이션입니다.

### @Column(name = "email", nullable = false, unique = true)
다음은 Spring JPA에서 제공해주는 DDL생성기능입니다. Spring JPA에서는 어플리케이션을 다시 시작할 때 도메인을 기반으로 이러한 DDL문을 통해 테이블을 새롭게 만들 수 있습니다. 물론 이런 DDL설정을 auto-create로 하지않는다면 필요없는 기능일 수 있습니다.

이러한 DDL설정 정보는 [JPA - 4장 - 엔티티 매핑](https://jacojang.github.io/jpa/java/hibernate/2016/12/01/jpa-chapter4-%EC%97%94%ED%8B%B0%ED%8B%B0_%EB%A7%A4%ED%95%91.html) 블로그에 잘나와있습니다. 여기에서 데이터스키마 자동생성 부분을 보시면 될것같습니다. 






어쨋든 여기에서 강조할점은 저런 설정들을 반드시하고 사용하는 데이터베이스와 DDL이 동기화가 되어야한다는것입니다. 그래야 실수를 줄일 수 있다고 생각하기 때문입니다. 예를들어 NULL이 들어가면안되는 컬럼인데 NULL이 들어가고 있다고 가정해보겠습니다. 저런 제약조건들이 없으면 이것을 인식하는데 오래 걸려 치명적인 버그로 이어질 수 있습니다. 차라리 Exception 나서 빠르게 버그를 수정하는게 좋습니다.

그리고 또한 같이 일하는 개발자들이 필드만 보고 바로 이 필드에 제약조건을 알 수 있기 때문에 정의해주는게 좋습니다.


## 마치며
이장에서 회원가입및 조회기능까지 작성하려고했었는데 내용이 너무 길어져 여기에서 마무리를 해야할것같습니다. 어쨋든 이장에서 좀 강조 하고싶은점은 프로그래밍을 하는 부분에서 버그를 만들지않는게 중요하다고 생각합니다. 물론 누구나 실수를 하게되고 버그를 만들 수 있습니다. 저 또한 그렇습니다. 

하지만 위에서 도메인을 정의할 때 처럼 사소한 하나하나의 규약을 정의하고 지키는게 정말 중요하다고 생각합니다. 그런 사소한 규약으로 버그를 줄일수 있다고 생각합니다. 그렇지 않으면 그러한것들이 버그로 이어진다고 생각합니다. 그래서 연관 있는 객체들의 데이터들을 임베디드화 하여 규합하고 응집력을 높여 유지보수하기 좋은 코드를 만듬으로써 실수들이 나올 확률을 줄이는게 중요하다고 생각합니다. 

이런점에서 객체지향 프로그래밍을 하게되면 나름대로의 규약을 만들어낼 수 있게 해주는것 같습니다.
