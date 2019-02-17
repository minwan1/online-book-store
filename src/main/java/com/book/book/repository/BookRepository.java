package com.book.book.repository;

import com.book.book.domain.Book;
import com.book.book.domain.BookId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, BookId> {


}
