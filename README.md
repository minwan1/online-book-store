[![Build Status](https://travis-ci.com/minwan1/online-book-store.svg?branch=master)](https://travis-ci.com/minwan1/online-book-store)
[![Coverage Status](https://coveralls.io/repos/github/minwan1/online-book-store/badge.svg?branch=master)](https://coveralls.io/github/minwan1/online-book-store?branch=master)

# 온라인 서점 API 만들기로 살펴보는 Spring Boot OOP

# 1. Spring을 접하면서
제가 처음 Spring 을 접한 것은 학교 연계 SI 회사 인턴생활이었습니다. 처음 Spring을 접했을 때는  정말 신세계였었습니다. 학교에서 배웠던 JSP를 이용하여 힘들게 만들던 URL매핑도 어노테이션 하나로 쉽게 구현할 수 있고 Mybatis를 이용하면 별다른 커넥션 관리없이 XML에 쿼리만 짜더라도 DB CRUD가 가능했기때문입니다.

하지만 Spring, Mybatis 아쉬운게 있다면 뭔가 OOP와 괴리감이 있다는 생각이 들었습니다. 제가 OOP에대한 경험이 많이 부족했어서 그럴수있습니다. 물론 아직도 부족합니다. Mybatis로도 고수라면 OOP적인 프로그래밍이 가능할것입니다. 하지만 대학생활에 배웠던 [난 정말 자바를 공부한적이 없어요](https://book.naver.com/bookdb/book_detail.nhn?bid=6056781) 라는 책에서 도서관리 프로그램을 만들 때 이어진 매끄러운 객체들간의 연계는 없어지고 그저 일정에 쫓겨 @Autowired를 이용한 싱글톤 서비스객체를 생성하고 if문으로 모든 로직을 해결하려는 소스들만 보게되었습니다. 

# 2. Spring Boot, Spring Data JPA를 접하면서
그 이후 Spring Boot와 Spring Data JPA를 접면서 OOP프로그래밍이 스프링에서도 가능하다는것을 느끼게된 계기인것같습니다. 아무래도 JPA를 통하여 기존에 있었던 객체와 관계형 데이터베이스간에 패러다임 불일치 문제를 해결 해줌으로써 제가 좀 더 쉽게 객체기반에 프로그래밍에 접근하게 해줬던것같습니다.

# 3. 프로젝트 목적 (객체지향적인 API서버 만들기)
서론이 많이 길었던 것 같습니다(__). 결론은 Spring Boot와 Spring Data JPA를 통해 제가 배우고 있는 것들을 기반으로 최대한 객체지향적인 온라인 서점 API 만들려고 합니다. 아무래도 기존에 Spring 특성을 이용해 게시판 만들기 등의 좋은 책들이 많이 나와있기 때문에 Spring의 특성보다는 저는 최대한 객체지향적인 온라인 서점 API 서버를 만드는 것에 중점을 두려고 합니다. 연재하면서 부족한 부분이 많으니 잘 알지 못하고 있는 부분에 대해 조언해주시면 감사히 받겠습니다.  

또 Spring을 객체지향적으로 코딩하고 싶은 분 처음 Spring Boot와 Spring Data JPA를 접하는 분들에게 조금이라도 도움이 되는 글이 되었으면 좋겠습니다!!

이 스키마 구조는 최범균님의 책 [DDD-START](https://book.naver.com/bookdb/book_detail.nhn?bid=10615650)를 많이 참조했고 이 연재에서도 내용들을 많이 참조하려고합니다. 공감가는 내용이 많고 너무 좋은 내용들이 많은것같습니다. 좀 더 심도있게 보고싶은분들은 이 책을 보시는것을 추천해드립니다.

# 4. 프로젝트 목차

1. [step-01 : 프로젝트 설계 및 프로젝트 생성](./doc/book-1.md)
2. [step-02 : 회원 엔터티 정의](./doc/book-2.md)
3. [step-03 : 회원가입 Controller 만들기 : 표현영역 효율적으로 관리하기](./doc/book-3.md)
4. [step-04 : 회원가입 Service 만들기 : 응용영역 효율적으로 관리하기 - Part1](./doc/book-4.md)
5. [step-04 : 회원가입 Service 만들기 : 응용영역 효율적으로 관리하기 - Part2](./doc/book-5.md)
6. [Step-05 : 테스트 코드 작성하기](./doc/book-6.md)
6. [Step-06 : 주문하기 기능 구현하기 - 1](./doc/book-7.md)


**step-XX Branch** 정보를 의미합니다. 보고 싶은 목차의 Branch로 checkout을 해주세요

* 지속해서 해당 프로젝트를 이어 나아갈 예정이라 깃허브 Start, Watching 버튼을 누르시면 구독 신청받으실 수 있습니다. 

# 5. 사용 기술 및 툴
* Spring Boot
* Spring Data JPA
* JAVA 8
* Mockito
* H2(Database)
* Swagger(추가 예정)
* IntelliJ IDEA Ultimate

# 6. 프로젝트 실행환경
Lombok이 반드시 설치 되있어야 합니다.
* [Eclipse 설치 : [lombok] eclipse(STS)에 lombok(롬복) 설치](http://countryxide.tistory.com/16)
* [Intell J 설치 : [Intellij] lombok 사용하기](http://blog.woniper.net/229)


# 7. 실행 방법
> Intellij : Open -> Application.class 실행
> Eclips, STS : FILE -> Import -> Maven -> Existing Maven Projects -> Application.class 실행

> 이프로젝트 따로 화면을 구성하지는 않습니다. 오직 REST API서버만을 구성할것입니다. API호출은 Swagger를 통해서 이루어질 것 입니다.


# 8. 질문 및 조언
질문 및 조언[팁, 조언]은 아래에 보이는 스샷처럼 이슈 발급하기를 눌러주시면 감사하겠습니다(__)

![](https://i.imgur.com/YzqV42R.png)

# 9. API Swagger

프로젝트를 받으시고 실행후 http://localhost:8080/swagger-ui.html 에서 API Call 테스트를 해보실 수 있습니다. <br>
* Swagger설치 방법은 [브록-1](https://github.com/minwan1/online-book-store/blob/step-04/doc/%EB%B8%8C%EB%A1%9D-1.md) 참조해주시면 감사하겠습니다.

# 10. 실행
```
$ mvn spring-boot:run
```

