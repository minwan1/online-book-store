# Step-06 : 주문하기 기능 구현하기 - 1
지금까지 유저에 대한 회원가입에 대한 로직과 테스트 코드를 작성했습니다. 이번 장부터 online-book-store에 핵심 기능인 주문하기 기능을 만들어보겠습니다. 또한 Spring 부트 버전을 기존 1.5.9 -> 2.1.0으로 올리겠습니다.

## 책, 주문 도메인 정의하기
### 책 도메인 정의하기
먼저 주문하기 기능을 구현하기 전에 처음 설계했던 도메인 설계를 다시 봐보겠습니다.

![](https://camo.githubusercontent.com/696914035d284a293dccdb2bcc56d3257c93c91c/68747470733a2f2f692e696d6775722e636f6d2f4851356b46494f2e706e67)

위에 도메인 설계에서 볼 수 있듯이 회원이 주문을 하기 위해서는 책 데이터가 필요합니다. 그렇기 때문에 아래와 같이 책 도메인을 먼저 정의해야 합니다. 테이블 정의에 대한 상세정보는 [여기](https://github.com/minwan1/online-book-store/blob/master/doc/book-1.md)에서 볼 수 있습니다.

* bookId : 책에 대한 id값입니다.
* name : 책에 이름입니다.
* price : 해당 책에 가격입니다.


```java
@Getter
@Entity
@Table(name = "book")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Book {

    @EmbeddedId
    private BookId bookId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "price", nullable = false)
    private Money price;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

}
```

아래는 책 도메인에 embedded id입니다.

```java
@Getter
@Embeddable
@Access(AccessType.FIELD)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BookId implements Serializable {

    @Column(name = "book_id")
    private String id;

    private BookId(String id) {
        this.id = id;
    }

    public static BookId generate() {
        return new BookId(UUID.randomUUID().toString());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookId bookId = (BookId) o;
        return Objects.equals(id, bookId.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

```

위와 같이 DB에 의존하는 게 아닌 애플리케이션 쪽에서 id를 생성하는 이유는 다음과 같은 장점을 가지기 때문입니다.

1. DB에 의존적인 방식은 DB의 insert 쿼리가 실행되어야 식별자가 생성됩니다. 즉 DB 식별자 생성 방식은 도메인 객체를 Repository에 저장할 때 식별자가 생성되는 반면 위와 같은 직접적인 id 생성 방법은 Repository에 저장하기 전에 애플리케이션 레벨에서 id 값을 얻어 낼 수 있습니다.

2. id 객체에 비지니스로직을 넣을 수 있습니다. 예를 들어 해당 id는 몇 글자 이내이면서 유저 id와 쿠폰 종류 기반으로 만들어진다고 해보겠습니다. 그러면 이 id만 보더라도 어떤 유저에 어떤 쿠폰 종류이겠구나를 예측할 수 있게 됩니다.


### 주문 도메인 정의하기
그런 다음 책을 주문하기 위해서 주문 도메인을 정의해야 합니다. 주문 엔터티 정보는 아래와 같습니다.

* OrderNumber : 유저가 주문한 주문 번호입니다.
* totalAmount : 유저는 여러 개에 대한 책을 주문할 수 있고 이것에 합산 금액입니다.
* recipient : 책을 받을 수취인에 대한 정보입니다.
* orderer : 책을 주문한 사람에 대한 정보입니다.
* orderLines : 유저는 하나에 주문에서 여러 가지 책을 구매하거나 여러 개의 책을 구 매 할 수 있는데 이것을 연결해주는 라인입니다.
* orderStatusHistories : 주문 상태로 추적을 위해서는 주문 상태는 1:n 관계로 관리됩니다. (하나의 주문은 상태가 계속 변경되기 때문입니다.)

```java
@Entity
@Getter
@Table(name = "purchase_order")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order {

    @EmbeddedId
    private OrderNumber orderNumber;

    @Column(name = "total_amount")
    private Money totalAmount;

    @Embedded
    private Recipient recipient;

    @ManyToOne
    private Member orderer;

    @ElementCollection
    @CollectionTable(name ="order_line", joinColumns = @JoinColumn(name = "order_number"))
    @OrderColumn(name = "line_idx")
    private List<OrderLine> orderLines = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "order_number")
    private List<OrderStatusHistory> orderStatusHistories = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}

```


```java
@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderNumber implements Serializable {

    @Column(name="order_number")
    private String number;

    private OrderNumber(String number) {
        this.number = number;
    }

    public static OrderNumber of(String value) {
        return new OrderNumber(value);
    }

    public static OrderNumber generateOrderNumber(){
        return new OrderNumber(RandomString.make(8));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderNumber orderNo = (OrderNumber) o;
        return Objects.equals(number, orderNo.number);
    }

    @Override
    public int hashCode() {
        return Objects.hash(number);
    }

}
```

머니에 대한 벨류입니다.
```java
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Money {

    private int value;

    public Money(int value) {
        this.value = value;
    }

    public static Money of(Integer value) {
        return new Money(value);
    }
}

```

수취인에 대한 벨류입니다.

```java
@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Recipient {

    @Valid
    @Embedded
    private Mobile mobile;

    @Valid
    @Embedded
    private Name name;

    @Valid
    @Embedded
    private Address address;

    @Column(name = "shipping_message")
    private String shippingMessage;
}

```

주문라인에 대한 벨류입니다.

```java
@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderLine {

    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false, updatable = false)
    private Book book;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "amount", nullable = false)
    private Money amount;

    public OrderLine(final Book book, final int quantity) {
        this.book = book;
        this.quantity = quantity;
        this.amount = book.calculate(quantity);
    }

    public static OrderLine of(final Book book, final int quantity) {
        return new OrderLine(book, quantity);
    }
}
```

주문상태에 대한 엔터티입니다.
```java
@Getter
@Entity
@Table(name = "order_status_history")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderStatusHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_status", nullable = false)
    private OrderStatus orderStatus;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at" , nullable = false)
    private LocalDateTime updatedAt;

    public OrderStatusHistory(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public static OrderStatusHistory of(OrderStatus orderStatus){
        return new OrderStatusHistory(orderStatus);
    }
}

```

## 벨류와 엔터티 구별하기
**우리가 요구 사항을 기반으로 도출한 모델은 크게 엔터티와 밸류로 구분될 수 있습니다.** 이 둘을 나누는 기준은 모델이 식별자를 가지고 있느냐 없느냐입니다. 이러한 구분을 하는 이유는 밸류 타입과 엔터티 타입을 잘나누어야 올바른 도메인 설계를 할 수 있기 때문입니다.

### 엔터티란
위에서 말한 대로 엔터티의 가장 큰 특징은 식별자를 갖는 것입니다. 주문은 고유의 식별자 즉 주문 번호를 가지고 있습니다. 예를 들어 주문을 하고 주문 정보를 변경하더라도 고유의 주문번호는 변경되지 않습니다. 주문이 삭제되지 않는 이상 이 식별자는 계속 유지될 것입니다.

### 벨류 타입란
반대로 벨류 타입은 고유한 식별자를 가지지 않습니다. 예를 들어 위에 수취인 도메인이 대표적인 value 타입일 수 있습니다. 수취인은 이름, 모바일, 주소 정보를 가지고 있는데요. 이것은 단순 받는 사람에 대한 데이터 정보입니다. 물론 요구 사항에 의해 이 값이 정규 화해야 할 대상이고 또 식별 가능해진 수취인이라면 엔터키가 될 수 있습니다. 하지만 이번 요구 사항에서는 단순 수취인에 대한 정보로만 사용되기 때문에 벨류 타입으로 정의했습니다.

물론 단순 정보면 아래같이 변수로 선언하지 굳이 클래스로 뺄 필요가 있느냐고 생각하실 수 있습니다.

```java
...
public class Order{

    @EmbeddedId
    private Ordernumber orderNumber
    ...

    private String recipientMobile;
    private Name recipientName;
    private Address recipeintAddress;
    ...
}
```

굳이 위와 같이 할 수 있는데 클래스로 뺀 이유는 전장에서도 말했던 응집력에 이유입니다. 처음 수취인 객체가 생성되거나 수취인 정보를 API를 통해 제공하기 위해서는 최소 2개의 DTO가 생성되어야 합니다. 또 요구 사항 변경으로 수취인에 컬럼이 추가되거나 제거해야 할 일 이 있을 수도 있습니다. 

그렇게 되면 해당 DTO, 도메인들에 컬럼을 제거하거나 수정해야 하는데 이러한 문제는 버그를 만들기 아주 좋은 구조입니다. 즉 응집력이 떨어지는 구조를 가지게 됩니다. 하지만 위와 같이 Recipient를 클래스로 가져가게 되면 Recipient 객체 수정만 해주면 되기 때문에 응집력을 높일 수 있습니다.

**Money**
또한 벨류 타입이 꼭 두 개 이상의 데이터를 가져야 하는 것은 아닙니다. 좀 더 해당 데이터에 의미를 명확하게 하거나 안전성을 높이기 위해 불변 객체일 필요가 있을 때 사용할 수 있습니다. 대표적인 예가 위에서 볼 수 있는 Money 객체입니다.

1. 위에 OrderLine에서 amounts는 얼핏 보면 이게 금액의 양을 의미하는지 물건의 양을 의미하는지 알 수 없지만 Money라는 클래스로 가져감으로써 이 변수에 의미가 돈의 양이라는 것이 좀 더 명확해졌습니다.

2. 만약 amount 또는 price를 단순 int 또는 long 타입으로 가져갔다면 누구나 쉽게 접근해서 변경할 수 있습니다. 하지만 Money라는 타입을 불변(immutable) 타입으로 가져가게 되면서 이 돈에 대한 정보를 갱신하기 위해서는 새로운 머니 객체를 만들어야 변경할 수 있게 되었습니다. 물론 이러한 객체의 안전성을 지키기 위한 불변에 특징은 벨류 타입뿐만 아니라 엔터 타입에서도 널리 활용될 수 있습니다.



## 책생성 API 만들기
주문을 하기 위해서는 책이 있어야 합니다. 보통 실무에서는 이러한 정규화된 책 데이터를 데이터베이스에 넣는 기능은 어드민에서 넣거나 액셀 파일 등을 통해 데이터베이스에 책 정보를 넣습니다. 여기에서는 간단하게 책 생성 API를 만들어보겠습니다. 먼저 API Controller를 정의했습니다.

```java
@RestController
@AllArgsConstructor
@RequestMapping("/books")
public class BookController {

    private final BookCreateService bookCreateService;
    private final BookHelperService bookHelperService;

    @PostMapping
    public BookResponse createBook(@RequestBody final BookCreateRequest request){
        return BookResponse.of(bookCreateService.createBook(request));
    }

}

```

실제로 책을 생성하는 서비스입니다.

```java
@Service
@AllArgsConstructor
public class BookCreateService {

    private final BookRepository bookRepository;

    public Book createBook(BookCreateRequest request){
        final Book book = Book.of(request);
        return bookRepository.save(book);
    }
}

```
아래는 Book 도메인에 추가 된 메소드들입니다.
```java
 private Book(String name, Money price) {
        Assert.notNull(name, "name must not be null!");
        Assert.notNull(name, "price must not be null!");
        this.bookId = BookId.generate();
        this.name = name;
        this.price = price;
    }

    @Builder
    public Book(BookId bookId, String name, Money price, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.bookId = bookId;
        this.name = name;
        this.price = price;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Book of(BookCreateRequest request) {
        return new Book(request.getName(), request.getPrice());
    }

    public Money calculate(int quantity) {
        return Money.of(price.getValue() * quantity);
    }
```

## setter 사용하지 않기
프로그래밍 책이나 예제들을 보면 객체를 기본 생성자를 통해 객체를 생성하고 무분별하게 setter를 통해 객체의 상태 값을 변경하고 데이터베이스에 저장하는 경우를 봤을 것입니다. 사실 이렇게 되면 더 이상 객체를 객체로 바라보지 않게 되고 단순 데이터의 모음집 바라보게 되며 절차 지향 프로그래밍을 하게 됩니다. 기본 생성자를 열어주고, setter를 사용하면 어떠한 단점들을 가지는지 좀 더 세부적으로 알아보겠습니다.

1. 불안전한 객체를 생성하게 됩니다.
예를 들어 주문 객체를 기본 생성자를 통해 생성한다고 해보겠습니다. 그리고 다음과 같이 객체를 생성하고 주문 객체를 데이터베이스에 저장한다고 해보겠습니다.

```java
Order order = new Order();
List<Orderline> lines = Array.asLine(new OrderLine());
order.setOrderLine(lines)
orderRepository.save(order);
```

주문을 하기 위해서는 주문한 책의 정보 및 수량이 있어야 하는데 다음과 같이 Orderline을 기본 생성자를 통해 무분별한 하게 객체를 생성한 후 데이터베이스 저장하고 있습니다. 이렇게 무분별한 객체 생성을 막지 못합니다.

2. 도메인에 무분별한 변경을 막을 수 없습니다.
유저가 주문을 하고 주문에 대한 수취인 정보를 변경할 수 있습니다. 단, 비즈니스적 요구상 주문 상태는 배송 중이거나 배송 완료일 때는 수취인 정보를 변경할 수 없습니다. 하지만 의미 없는 setter 기능을 제공한다면 이러한 비즈니스적 규칙 없이 배송 상태를 변경할 수 있기 때문에 큰 실수를 할 수 있습니다.

처음 들어온 개발자 혹은 기존 개발자라고 해도 모든 비즈니스적 도메인 규칙을 외우고 있을 수 없기 때문에 아래와 같이 명시적으로 기능을 제공해준다면 실수를 할 확률을 줄일 수 있습니다.

또한 객체지향적인 관점에서 보더라도 단순 setter를 통한 주문 상태 데이터 변경이 아닌 객체한테 시킨다는 관점 즉 행위 기반 changeRecipient는 표현이 올바르기 때문입니다.(결국 소스를 유지 보수하는 것은 컴퓨터가 아닌 사람이기 때문에 setter보다는 아래와 같이 가독성 좋은 changeRecipient 같은 메소드명을 사용하는 것이 좋습니다.)

```java
public class Order(){
    ...
    private OrderStatus orderStatus;
    private Recipient recipient;

    public void changeRecipient(Recipient recipient){
        if(orderStatus == DELIVERING || orderStatus == DELIVERY_COMPLETED){
            throw new OrderStatusChangeException("
The recipient information can not be changed if the order status is DELIVERING or DELIVERY_COMPLETED.");
        }
        this.recipient = recipient;
    }
}
```

3. 해당 도메인에 변경사항을 추적할 할 수 없습니다.
도메인을 행위(기능)으로 변경이 아닌 setter를 통해 데이터를 변경한다면 해당 도메인이 무슨 기능을 제공하는지 알 수 없습니다. 무분별하게 setter가 난발되었기 때문입니다.

```java
public class Order{
    ...//멤머 필드
    ...// getter, setter
}
```
위와 같은 poor 한 객체가 있다고 하면 이 주문 객체가 무슨 기능을 제공하는지, 무슨 도메인에 비즈니스적 규칙들이 있는지 알 수 없습니다. 물론 주문 관련된 모든 서비스 레이어들을 확인한다면 해당 기능을 찾을 수 있지만 너무 많은 시간과 노력 또 응집력이 떨어지기 때문에 요구 사항 변경이 있다면 버그가 일어날 확률이 너무 큽니다.


이러한 이유로 Book 도메인 setter를 지양한 불변 객체로 가져갔습니다. 그렇기 때문에 이 Book을 생성하기 위해서는 of라는 함수 또는 새로운 요구 사항에 의해서만 Book이 생성될 수 있습니다. 이렇게 함으로써 유지 보수하거나 새로운 개발자 입장에서는 도메인을 보고 of라는 함수에 명세된 파라미터 값으로 만 book 객체가 생성될 수 있는 것을 알 수 있습니다.

### Book 생성 API호출
[online-book-store](https://github.com/minwan1/online-book-store) 앱을 받거나 직접 소스를 완성한 후 아래와같이 [Swagger Book API](http://localhost:8080/swagger-ui.html#!/book45controller/createBookUsingPOST)를 통해 책 데이터를 데이터베이스 등록해보겠습니다.

![](https://i.imgur.com/MVn3O9p.png)

책 생성 API가 성공했다면 아래와 같은 response를 받을 수 있습니다. 물론 id는 조금 다를 수 있습니다.
```json
{
  "bookId": {
    "id": "f56cc650-cc8b-4266-94ad-51212930e8e5"
  },
  "name": "online-book-store(wan)",
  "price": {
    "value": 1000000
  },
  "createdAt": "2019-02-17T17:53:24.237",
  "updatedAt": "2019-02-17T17:53:24.24"
}
```
그러면 다음 편에서 이 책 id를 통해 주문 API 기능을 만들어 보겠습니다.