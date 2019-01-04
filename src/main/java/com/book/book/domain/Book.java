package com.book.book.domain;


import com.book.book.dto.BookCreateRequest;
import com.book.order.domain.Money;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.util.Assert;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDateTime;

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


    private Book(String name, Money price) {
        Assert.notNull(name, "name must not be null!");
        Assert.notNull(name, "price must not be null!");
        this.bookId = BookId.generate();
        this.name = name;
        this.price = price;
    }

    public static Book of(BookCreateRequest request) {
        return new Book(request.getName(), request.getPrice());
    }

    public Money calculate(int quantity) {
        return Money.of(price.getValue() * quantity);
    }
}
