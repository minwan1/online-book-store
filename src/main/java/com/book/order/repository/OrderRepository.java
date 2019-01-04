package com.book.order.repository;

import com.book.order.domain.Order;
import com.book.order.domain.OrderNumber;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, OrderNumber> {


}
