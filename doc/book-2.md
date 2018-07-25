# Step02 -  회원 엔터티 정의

# 1. 회원 엔터티 정의  
먼저 앞에서 설계 테이블을 간단히 보겠습니다. 다음과 같은 member 테이블이 있었습니다.

<p align="center">![](https://i.imgur.com/Aa1gDEY.png)</p>

회원을 가입시키기 위해서는 member 엔터티를 정의해야할 필요가 있습니다. 위 그림 테이블을 기반으로 Member 엔터티를 정의해볼것입니다.

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
위에 정의되어있는 Member클래스에 어노테이션들이 어떻게 정의되었는지, 어떤 용도인지 알아보겠습니다.

## @Entity
JPA를 통해 관리할 클래스들을 명시적으로 @Entity라고 선언하는 것입니다. 도메인을 Entity로 사용하기 위해서는 필수로 입력하셔야 합니다.

## @Table
테이블어노테이션은 엔터티와 매핑할 실제 디비 테이블을 지정합니다. 위에서 정의한 도메인은 member를 지정함으로써 테이블 member와 Entity Member와 매핑이됩니다. JPA에서 테이블을 새로 만들어주는 기능이 있는데 이것을 이용해 테이블을 생성하면 name에 value를 기반으로 테이블을 만들어줍니다.

## @NoArgsConstructor(access = AccessLevel.PROTECTED)
다음은 파라미터가없는 기본생성자를 만들어주는 어노테이션입니다. Spring JPA를 사용하기위해서는 반드시 기본생성자가 필요합니다. Spring JPA는 DB에서 데이터를 읽어와 도메인에 매핑할 때 기본생성자를 사용해서 객체를 생성해 데이터를 매핑합니다. 그렇기 때문에 반드시 기본생성자가 필요합니다. 롬복을 사용하지않고 java로 표현하면 다음과 같이 기본생성자를 protected로 선언하는것과 같습니다.

```java
protected Member(){

}
```

여기에서 굳이 접근 레벨을 Protected로 한 이유는 애플리케이션 내에서 이유없이 도메인을 생성 하는것을 막으려했기 때문입니다. Member가 만들어지기 위해서는 필수적인 정보가 있을 텐데 기본 생성자를 이용해서 객체를 만들면 불안전한 객체가 생성되기 때문입니다.

그리고 Protected로 생성자를 지정하면 같이 개발하는 개발자들이 적어도 이객체는 기본생성자로 생성해서는 안되는 객체구나라고 생각할 수 있습니다.

## @Id
JPA에서 해당 필드에 기본키를 지정하기위해서 @Id 어노테이션을 지정해야합니다.

### @GeneratedValue(strategy = GenerationType.IDENTITY)
@GeneratedValue(strategy = GenerationType.IDENTITY) 지정하면 mysql의 AUTO_INCREAMENT와 같은기능으로 데이터베이스가 기본키를 생성해줍니다. 예를 들어 회원을 2명 생성해주면 자동으로 2번째 회원을 생성할 때는 id를 2값으로 회원을 생성해줍니다.


## @Embedded

### 데이터의 응집력, 데이터타입 규합
Spring Data JPA Embedded어노테이션이 있음으로써 Spring Data JPA는 좀더 객체지향적으로 프로그래밍하게 해줍니다. 데이터타입을 규합시켜주거나 객체의 응집력을 높여주기 때문입니다. 어떻게 `데이터타입을 규합`시키고, `객체의 응집력`을 높이는지 예제를통해 알려드리겠습니다.

### Eamil class
아래와 같이 Email 클래스가 있습니다. 이것은 Member 클래스에 Embedded 되어 있는 Email 클래스입니다. 이렇게 클래스형으로 가지고있다고해도 JPA는 쉽게 데이터베이스에 데이터를 CRUD를 할 수 있습니다. 그리고 단순히 String이 아닌 Email을 클래스로하는 것에 대해 너무 과하지 않냐고 생각하실 수 도 있습니다.

>Email 클래스에서 @org.hibernate.validator.constraints.Email 어노테이션과 @NotEmpty 어노테이션이 있습니다. 이 어노이션은 클라이언트로부터 넘어온 데이터가 email 형식이 맞는지 value의 값이 들어있는지 validation을 체크해주고 유효하지 않으면 Exception을 발생시켜 클라이언트한테 이메일 형식이 유효하지 않다고 알려주는 역할을 합니다.

예를 들어 만약 Email을 String 타입으로 선언했고, Member 클래스가 아닌 Recipient 클래스 등이 Email을 멤버 필드로 필요로 한다고 가정해보겠습니다. 그러면 Member 클래스, Recipient 클래스에도 똑같이 이메일 validation 체크 어노테이션등을 해줘야하고 이메일에 필요한 host 정보를 가져온다거나 id 정보만 가져와야 하는 똑같은 기능들의 메소드를 두 클래스에 구현해줘야 할 것입니다. 이렇듯 Email을 클래스로 가져가지 않게 되면 데이터의 응집성이 떨어지면서 반복 코드가 발생하게 됩니다.

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

### Name class
하나 더 예를들어 아래의 Name클래스를 봐보겠습니다. Name 클래스는 다음과 같이 있습니다.
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

회사 제품이 갑자기 글로벌 시장 진출로 인해 기존 firstname, lastname만 받는 게 아닌 middle_name을 받아야 한다고 가정해보겠습니다. 만약 프로젝트에서 DTO를 쓴다면 모든 곳에서 middleName을 넣어줘야 하는 문제가 생길 것입니다. 하지만 name을 클래스로 가져갔다면 유연하게 변경할 수 있습니다. 그 이유는 Name을 쓰는 곳은 하나의 클래스이기 때문입니다.


이렇게 2가지정도 예를들어 Embedded class를 사용하는 이유에대해 간단히 설명을 들였는데요. 처음에 말한것처럼 Embedded class는 좀 더 개발을 객체지향스럽게 해주는 아주 좋은 어노테이션이라고 생각합니다. 객체의 응집력을 높임으로써 변경에 유연하고, 객체 기능의 확장을 쉽게 해주기 때문입니다.

그렇다고 해서 모든 것을 Embedded 타입으로 가져가자는 의미는 아닙니다!! 의미있는 데이터끼리 또는 위와같이 기능의 확장성이 필요로하는 데이터를 잘 선별하여 Embedded 타입으로 뺄 수 있다고 생각합니다. 

그리고 여기에서 나온 validate 어노테이션은 다른 장에서 좀 더 자세히 알아보겠습니다.


## @CreationTimestamp, @UpdateTimestamp
어노테이션은 만들어진 시간과 데이터 변경시간을 자동으로 처리해주는 어노테이션입니다.

## @Column(name = "email", nullable = false, unique = true)
다음은 Spring JPA에서 제공해주는 DDL 생성 기능입니다. Spring JPA에서는 애플리케이션을 다시 시작할 때 도메인을 기반으로 이러한 DDL 문을 통해 테이블을 새롭게 만들 수 있습니다. 물론 이런 DDL 설정을 auto-create로 하지 않는다면 필요 없는 기능일 수 있습니다. 하지만 데이터베이스와 Entity에 DDL 설정을 동기화하는 것을 추천드립니다.

그래야 크리티컬한 버그 줄일 수 있다고 생각하기 때문입니다. 예를 들어 NULL이 들어가면 안 되는 칼럼인데 개발자도 모르게 칼럼에 NULL이 들어가고 있다고 가정해보겠습니다. 위와 같은 제약조건들이 없으면 이것을 인식하는데 오래 걸려 치명적인 버그로 이어질 수 있습니다. 차라리 DBMS 제약조건 Exception 나서 빠르게 버그를 수정하는 것이 낫습니다.

>DDL설정 정보는 [JPA - 4장 - 엔티티 매핑](https://jacojang.github.io/jpa/java/hibernate/2016/12/01/jpa-chapter4-%EC%97%94%ED%8B%B0%ED%8B%B0_%EB%A7%A4%ED%95%91.html) 블로그에 잘나와있습니다. 여기에서 데이터스키마 자동생성 부분을 보시면 될것같습니다.




## 마치며
이장에서 엔터티 및 도메인을 정의하는 방법에 대해 설명했습니다. 사실 이장에서 객체지향도 중요하지만 좀 더 강조하고 싶은 것은 `프로그래밍을 하는 부분에서 실수를 만들지 않기 위해 노력을 하자`입니다. 물론 누구나 `실수`를 하게 되고 버그를 만들 수 있습니다. 하지만 이 `실수`나 버그가 다시 발생하지 않기 위해 어떤 식으로 노력할 것이냐 입니다.

1. 데이터의 응집력을 생각하지 않은 코딩으로 인한 `사이드이펙트 남발`
2. public 기본 생성자로 Entity 객체 생성 남발로 인한 `불안전한 엔터티 생성`
3. 무심한 DDL 설정으로 인한 `더 큰 버그 발생`

사실 위에 `실수`들은 제약조건을 코드로 강제하지 않는다면 개발하면서 계속 다시 발생할 수 있는 `실수`들입니다. 위와 같은 `실수`들을 다시 발생하는 것을 막기 위해 위 예제에서는 코드로 제약조건을 강제함으로써 같은 `실수`를 반복하지 못하도록 막았습니다. 이렇듯 다시 나올 수 있는 `실수`에 대해서는 어떻게 재발하는 것을 막을지에 대해 항상 생각한다면 좀 더 안정적인 프로그래밍을 만들 수 있다고 생각합니다.
