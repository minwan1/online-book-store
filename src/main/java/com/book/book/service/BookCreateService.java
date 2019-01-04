package com.book.book.service;

import com.book.book.domain.Book;
import com.book.book.dto.BookCreateRequest;
import com.book.book.repository.BookRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class BookCreateService {

    private final BookRepository bookRepository;

    public Book createBook(BookCreateRequest request){
        final Book book = Book.of(request);
        return bookRepository.save(book);
    }
}
