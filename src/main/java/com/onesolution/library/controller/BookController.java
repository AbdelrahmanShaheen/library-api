package com.onesolution.library.controller;

import com.onesolution.library.dto.BookRequest;
import com.onesolution.library.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {
    private final BookService bookService;

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public void addBook(@RequestBody BookRequest bookRequest) {
        bookService.addBook(bookRequest);
    }
}
