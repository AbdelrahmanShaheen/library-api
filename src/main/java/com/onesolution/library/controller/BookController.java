package com.onesolution.library.controller;

import com.onesolution.library.dto.BookRequest;
import com.onesolution.library.dto.BookResponse;
import com.onesolution.library.dto.BookTransactionRequest;
import com.onesolution.library.dto.BorrowedBookResponse;
import com.onesolution.library.entity.BookTransaction;
import com.onesolution.library.service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {
    private final BookService bookService;

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public void addBook(@RequestBody @Valid BookRequest bookRequest) {
        bookService.addBook(bookRequest);
    }
    @GetMapping("")
    public Page<BookResponse> getAllBooks(Pageable page) {
        return bookService.getAllBooks(page);
    }
    @GetMapping("/{id}")
    public BookResponse getBookById(@PathVariable Long id) {
        return bookService.getBookById(id);
    }
    @PutMapping("/{id}")
    public void updateBook(@PathVariable Long id, @RequestBody BookRequest bookRequest) {
        bookService.updateBook(id, bookRequest);
    }
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
    }
    @GetMapping("/search")
    public Page<BookResponse> searchBooksBy(@RequestParam(required = false) String title,
                                            @RequestParam(required = false) String genre,
                                            Pageable page)
    {
        return bookService.searchBooksBy(title,genre, page);
    }
    @PostMapping("/{id}/borrow")
    @ResponseStatus(HttpStatus.CREATED)
    public void borrowBook(@PathVariable Long id, @RequestBody BookTransactionRequest bookTransactionRequest) {
        bookService.borrowBook(id, bookTransactionRequest);
    }
    @PostMapping("/{id}/return")
    public void returnBook(@PathVariable Long id, @RequestBody BookTransactionRequest bookTransactionRequest) {
        bookService.returnBook(id, bookTransactionRequest);
    }
    @GetMapping("/borrowed")
    public Page<BorrowedBookResponse> getAllBorrowedBooks(Pageable page) {
        return bookService.getAllBorrowedBooks(page);
    }
}
