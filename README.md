# 온라인 서점 API 만들기로 살펴보는 Spring Boot OOP

## 1. Spring을 접하면서
처음 Spring을 접하고 정말 신세계였었습니다. 학교에서 배웠던 JSP를 이용하여 힘들게 만들던 URL매핑도 어노테이션 하나로 쉽게 구현할 수 있고 Mybatis를 이용하면 별다른 커넥션 관리없이 XML에 쿼리만 짜더라도 DB CRUD가 가능했기때문입니다.

하지만 Spring, Mybatis 아쉬운게 있다면 뭔가 OOP와 괴리감이 있다는 생각이 들었습니다. 제가 OOP에대한 경험이 많이 부족했어서 그럴수있습니다. 물론 아직도 부족합니다. mybatis로도 고수라면 oop적인 프로그래밍이 가능할것입니다. 하지만 대학생활에 배웠던 [난 정말 자바를 공부한적이 없어요](https://book.naver.com/bookdb/book_detail.nhn?bid=6056781) 라는 책에서 도서관리 프로그램을 만들 때 이어진 매끄러운 객체들간의 연계는 없어지고 그저 @Autowire를 이용한 싱글톤 서비스객체를 생성하고 if문으로 모든 로직을 해결하려는 소스들만 보게되었습니다. 앞얘기는 학교연계 SI회사 인턴생활중 겪었던 얘기입니다.

## 2. Spring Boot, Spring Data JPA를 접하면서
그 이후 Spring Boot와 Spring Data JPA를 접면서 OOP프로그래밍이 스프링에서도 가능하다는것을 느끼게된 계기인것같습니다. 아무래도 JPA를 통하여 기존에 있었던 객체와 관계형 데이터베이스간에 패러다임 불일치 문제를 해결 해줌으로써 제가 좀 더 쉽게 객체기반에 프로그래밍에 접근하게 해줬던것같습니다.



## 3. 프로젝트 목적 (객체지향적인 API서버 만들기)
서론이 많이 길었던것같습니다.(__) 결론은 Spring boot와 Spring Data JPA를 통해 제가 배우고 있는것들을 기반으로 최대한 객체지향적인 온라인 서점 API 만들려고합니다. 아무래도 기존에 Spring 특성을 이용해 게시판만들기등의 좋은책들이 많이 나와있기때문에 Spring의 특성보다는 저는 최대한 객체지향적인 온라인 서점 API서버를 만드는것에 중점을 두려고합니다. 연재하면서 부족한 부분이 많으니 잘알지 못하고있는부분에 대해 조언해주시면 감사히 받겠습니다. 

또 객체지향적으로 코딩하고싶은분 처음 Spring boot와 Spring Data JPA를 접하는분들에게 조금이라도 도움이 되는 글이 되었으면 좋겠습니다!!

이 스키마 구조는 최범균님의 책 [DDD-START](https://book.naver.com/bookdb/book_detail.nhn?bid=10615650)를 많이 참조했고 이 연재에서도 내용들을 많이 참조하려고합니다. 공감가는 내용이 많고 너무 좋은 내용들이 많은것같습니다. 좀 더 심도있게 보고싶은분들은 이 책을 보시는것을 추천해드립니다.


## 4. 사용 기술 및 툴
* Spring Boot
* Spring Data JPA
* JAVA 8
* Mockito
* H2(Database)
* IntelliJ IDEA Ultimate

## 5. 프로젝트 실행환경
Lombok이 반드시 설치 되있어야 합니다.
* [Eclipse 설치 : [lombok] eclipse(STS)에 lombok(롬복) 설치](http://countryxide.tistory.com/16)
* [Intell J 설치 : [Intellij] lombok 사용하기](http://blog.woniper.net/229)


## 6. 실행 방법
> Intellij : Open -> Application.class 실행
> Eclips, STS : FILE -> Import -> Maven -> Existing Maven Projects -> Application.class 실행

> 이프로젝 따로 화면을 구성하지는 않습니다. 오직 REST API서버만을 구성할것입니다. API호출은 Swagger를 통해서 이루어질 것 입니다.

## 7.프로젝트 목차

1. [프로젝트 설계 및 프로젝트 생성]("test")
2. [회원 엔터티 정의](")
3. [회원가입 Controller 만들기(표현영역 효율적으로 관리하기]()







### 8. 질문 및 조언
질문 및 조언[팁, 조언]은 아래에 보이는 스샷처럼 이슈발급하기를 눌러주시면 감사하겠습니다(__)
![](https://i.imgur.com/YJFBcbV.png)


