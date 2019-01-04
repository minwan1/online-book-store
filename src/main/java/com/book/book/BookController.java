package com.book.book;

import com.book.book.dto.BookCreateRequest;
import com.book.book.dto.BookResponse;
import com.book.book.service.BookCreateService;
import com.book.book.service.BookHelperService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping
    public List<BookResponse> findBooks(){
        return BookResponse.of(bookHelperService.findByAll());
    }

}
