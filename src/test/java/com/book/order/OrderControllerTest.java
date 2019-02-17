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
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(OrderController.class)
public class OrderControllerTest extends BaseControllerTest{

    @MockBean
    private OrderBookService orderBookService;

    @MockBean
    private OrderHelperService orderHelperService;

    @Autowired
    private WebApplicationContext context;

    private MockMvc mvc;

    private Member member;
    private Recipient recipient;
    private Address address;
    private Book book;
    private Order order;

    protected ObjectMapper objectMapper = new ObjectMapper();

    @Before
    public void setUp() {

        this.mvc = buildMockMvc(context);

        recipient = buildRecipient(null);
        address = buildAddress(null);
        book = buildBook();
        member = Member.builder().build();
        order = buildOrder();

    }

    @Test
    public void orderIsSuccess() throws Exception {

        //given
        final OrderRequest orderRequest = createOrderRequest(1l, Arrays.asList(buildOrderBook()), buildRecipient("city"));

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
        final OrderRequest orderRequest = createOrderRequest(1, Arrays.asList(buildOrderBook()), buildRecipient(null));

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

    private Recipient buildRecipient(final String city) {
        return Recipient.builder()
                .address(buildAddress(city)).build();
    }

    private Address buildAddress(final String city) {
        return Address.builder()
                .city(city)
                .street("street")
                .zipcode("zipcode").build();
    }

}