package com.book.order;

import com.book.order.domain.Order;
import com.book.order.domain.OrderNumber;
import com.book.order.dto.OrderRequest;
import com.book.order.dto.OrderResponse;
import com.book.order.service.OrderBookService;
import com.book.order.service.OrderHelperService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@AllArgsConstructor
@RequestMapping("/orders")
public class OrderController {

    private final OrderBookService orderBookService;
    private final OrderHelperService orderHelperService;

    @PostMapping
    public OrderResponse orderBook(@RequestBody @Valid final OrderRequest request){
        final Order order = orderBookService.oderBook(request);
        return OrderResponse.of(order);
    }

    @GetMapping("/{orderNumber}")
    public OrderResponse getOrder(@PathVariable final String orderNumber){
        final Order order = orderHelperService.findByOrderNumber(OrderNumber.of(orderNumber));
        return OrderResponse.of(order);
    }

}
