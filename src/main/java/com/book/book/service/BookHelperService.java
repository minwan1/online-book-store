package com.book.book.service;

import com.book.book.domain.Book;
import com.book.book.domain.BookId;
import com.book.book.exception.BookNotFoundException;
import com.book.book.repository.BookRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Transactional
public class BookHelperService {

    private final BookRepository bookRepository;

    public Book findById(final BookId id) {
        final Optional<Book> book = bookRepository.findById(id);
        book.orElseThrow(() -> new BookNotFoundException());
        return book.get();
    }

    public List<Book> findByAll(){
        return bookRepository.findAll();
    }


}
