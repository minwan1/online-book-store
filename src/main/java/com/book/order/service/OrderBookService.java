package com.book.order.service;

import com.book.book.domain.Book;
import com.book.book.service.BookHelperService;
import com.book.member.domain.Member;
import com.book.member.service.MemberHelperService;
import com.book.order.domain.Order;
import com.book.order.domain.OrderLine;
import com.book.order.domain.OrderNumber;
import com.book.order.dto.OrderBook;
import com.book.order.dto.OrderRequest;
import com.book.order.repository.OrderRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
public class OrderBookService {

    private final OrderRepository orderRepository;
    private final OrderHelperService orderHelperService;
    private final MemberHelperService memberHelperService;
    private final BookHelperService bookHelperService;

    public Order oderBook(final OrderRequest orderRequest) {
        final OrderNumber orderNumber = orderHelperService.generateCouponCode();
        final Member member = memberHelperService.findById(orderRequest.getMemberId());
        final List<OrderLine> orderLines = createOrderLines(orderRequest.getOrderBooks());
        final Order order = Order.of(orderNumber, member, orderLines, orderRequest.getRecipient());
        return orderRepository.save(order);
    }

    private List<OrderLine> createOrderLines(final List<OrderBook> orderRequest) {
        return orderRequest.stream()
                .map(orderBook -> {
                    final Book book = bookHelperService.findById(orderBook.getId());
                    return OrderLine.of(book, orderBook.getQuantity());
                }).collect(Collectors.toList());
    }

}
