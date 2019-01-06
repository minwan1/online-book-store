package com.book.order;

import com.book.book.domain.Book;
import com.book.book.domain.BookId;
import com.book.member.domain.Address;
import com.book.member.domain.Member;
import com.book.order.domain.*;
import com.book.order.dto.OrderBook;
import com.book.order.dto.OrderRequest;
import com.book.order.service.OrderBookService;
import com.book.order.service.OrderHelperService;
import com.book.test.BaseControllerTest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(OrderController.class)
public class OrderControllerTest extends BaseControllerTest {

    @Autowired
    private MockMvc mvc;
    @MockBean
    private OrderBookService orderBookService;
    @MockBean
    private OrderHelperService orderHelperService;


    private Member member;
    private Recipient recipient;
    private Address address;
    private Book book;
    private Order order;



    @Before
    public void setUp() throws Exception {

        recipient = buildRecipient();
        address = buildAddress();
        book = buildBook();
        member = Member.builder().build();
        order = buildOrder();

    }

    @Test
    public void orderIsSuccess() throws Exception {

        //given
        final OrderRequest orderRequest = createOrderRequest(1l, Arrays.asList(buildOrderBook()), buildRecipient());

        given(orderBookService.oderBook(any())).willReturn(order);

        //when
        mvc.perform(post("/orders")
                .content(objectMapper.writeValueAsString(orderRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        //then
    }


    @Test
    public void orderIsFail() throws Exception {

        //given
        final OrderRequest orderRequest = createOrderRequest(1, Arrays.asList(buildOrderBook()), buildRecipient());

        given(orderBookService.oderBook(any())).willReturn(order);

        //when
        mvc.perform(post("/orders")
                .content(objectMapper.writeValueAsString(orderRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());

        //then
    }



    private Order buildOrder() {
        return Order.builder()
                .createdAt(LocalDateTime.now())
                .orderer(member)
                .orderLines(Arrays.asList(OrderLine.of(book, 2)))
                .orderNumber(OrderNumber.generateOrderNumber())
                .orderStatusHistories(Arrays.asList(OrderStatusHistory.of(OrderStatus.PREPARING)))
                .recipient(recipient)
                .totalAmount(Money.of(6000))
                .updatedAt(LocalDateTime.now())
                .build();
    }

    private Book buildBook() {
        return Book.builder()
                .bookId(BookId.generate())
                .createdAt(LocalDateTime.now())
                .name("test")
                .price(Money.of(3000))
                .updatedAt(LocalDateTime.now())
                .build();
    }


    private OrderRequest createOrderRequest(final long memberId, final List<OrderBook> orderBooks, final Recipient recipient) {
        return OrderRequest.builder()
                .memberId(memberId)
                .orderBooks(orderBooks)
                .recipient(recipient)
                .build();
    }

    private OrderBook buildOrderBook() {
        return OrderBook.builder()
                .id(BookId.generate())
                .quantity(2)
                .build();
    }

    private Recipient buildRecipient() {
        return Recipient.builder()
                .address(buildAddress()).build();
    }

    private Address buildAddress() {
        return Address.builder()
                .city(null)
                .street("street")
                .zipcode("zipcode").build();
    }


    // 컨트롤러를 작성하는데 너무 많은 테스트 시간이 든다. 그렇기 때문에 컨트롤러는 차라리 통합테스트로하는게 낫다.
    // 서비스 관계설정이 상당히 복잡하다. 그 데이터들을 목데이터를 넣어주기는 너무 빡쎄다.
    // 컨트롤러에서는 크게 valid체크 exception과 서비스를 호출했을떄 exception이 발생할 수 있다.

    //통합테스트로 간다.
    // exception이 난다.
    //valid 체크
    //내부적인exception
    // 서버 exception



    // 빌더 다열어주고 좀 사용해보고 필요없으면 안해야지 ㅠ

}